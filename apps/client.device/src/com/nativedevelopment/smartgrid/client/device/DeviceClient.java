package com.nativedevelopment.smartgrid.client.device;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeviceClient extends Main implements IDeviceClient, IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";

	public static final String APP_SETTINGS_DEFAULT_PATH = "client.device.settings";
	public static final String APP_SETTINGS_RECOVERY_DEFAULT_PATH = "client.device.recovery.dump";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	private UUID a_oIdentifier = null;
	private UUID a_iSettings = null;
	private boolean a_bIsIdle = true;

	private Queue<Serializable> a_lDataQueue = null; // TODO IData
	private Queue<Serializable> a_lActionQueue = null; // TODO IAction
	private Queue<Serializable> a_lConfigureConnectionQueue = null; // TODO IConfigureConnection
	private Map<UUID, Serializable> a_lActionMap = null; // TODO iAction -> iInstruction
	private Map<UUID, IConnectionConstructor> a_lConnectionConstructors = null;

	protected DeviceClient() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_mConnectionManager = MConnectionManager.GetInstance();
	}

	public void ShutDown() {
		a_mConnectionManager.ShutDown();
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();
		a_mConnectionManager.SetUp();

		ISettings oDeviceClientSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		a_iSettings = oDeviceClientSettings.GetIdentifier();
		Configure(oDeviceClientSettings);

		a_lDataQueue = new ConcurrentLinkedQueue<>();
		a_lActionQueue = new ConcurrentLinkedQueue<>();
		a_lConfigureConnectionQueue = new ConcurrentLinkedQueue<>();
		a_lActionMap = new ConcurrentHashMap<>(); // TODO maybe not concurrent actions are fixed
		a_lConnectionConstructors = new ConcurrentHashMap<>();

		/* temporary configuration begin */
		Fx_RegisterConnectionConstructors();
		/* temporary configuration end */

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new DeviceClient();
		return a_oInstance;
	}

	private void Fx_RegisterConnectionConstructors() {
		// TODO violation coding use of lambda function
		ISettings oSettings = a_mSettingsManager.GetSettings(a_iSettings);
		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("data.realtime.producer.type")),
				oConfiguration -> {
					IConnection oConnection = new RabbitMQProducerConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetFromQueue(a_lDataQueue);
					return oConnection;
				});

		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("action.control.consumer.type")),
				oConfiguration -> {
					IConnection oConnection = new RabbitMQConsumerConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetToQueue(a_lActionQueue);
					return oConnection;
				});

		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("connection.configure.consumer.type")),
				oConfiguration -> {
					IConnection oConnection = new RabbitMQConsumerConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetToQueue(a_lConfigureConnectionQueue);
					return oConnection;
				});
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		if(oConnection == null) {
			a_mLogManager.Warning("attempt of a null connection",0);
			return null;
		}
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private ISettings Fx_CreateConnectionSettings(IConfigureConnection oConfigure) {
		ISettings oSettings = new Settings(oConfigure.GetIdentifier());
		for (ISetting oSetting : oConfigure.GetSettings()) {
			oSettings.Set(oSetting.GetKey(), oSetting.GetValue());
		}
		a_mSettingsManager.AddSettings(oSettings);
		return oSettings;
	}

	private void Fx_ConfigureConnection() {
		Serializable ptrConfigure =  a_lConfigureConnectionQueue.poll();
		if(ptrConfigure == null) {
			return;
		}
		IConfigureConnection oConfigure = (IConfigureConnection) ptrConfigure;
		IConnectionConstructor oConstructor = a_lConnectionConstructors.get(oConfigure.GetTypeIdentifier());
		if(oConfigure == null) {
			a_mLogManager.Warning("Constructor not available.",0);
			return;
		}
		Fx_EstablishConnection(oConstructor.New(oConfigure));
		a_bIsIdle = false;
	}

	private void Fx_PerformAction() {
		Serializable ptrAction = a_lActionQueue.poll();
		if(ptrAction == null) {
			return;
		}
		IAction oAction = (IAction) ptrAction;
		// TODO sub implementation
		a_bIsIdle = false;
	}

	private void Fx_ProduceData() {
		int nTuples = 1;
		IData oData = Generator.GenerateDataSensor(a_oIdentifier, nTuples);
		// TODO sub implementation
		a_lDataQueue.add(oData);
		a_bIsIdle = false;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable oIdentifier = oConfigurations.Get(SETTINGS_KEY_IDENTIFIER);
		a_oIdentifier = oIdentifier == null ? UUID.randomUUID() : UUID.fromString((String)oIdentifier);
		a_mLogManager.Success("configured",0);
	}

	@Override
	public void Run() {
		while(!IsClosing()) {
			// Timer.TimeOutProcedure(a_bIsIdle);
			a_bIsIdle = true;
			Fx_ProduceData();
			Fx_PerformAction();
			Fx_ConfigureConnection();
			Exit();
		}
	}

	public static void main(String[] arguments)
	{
		Main oApplication = DeviceClient.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
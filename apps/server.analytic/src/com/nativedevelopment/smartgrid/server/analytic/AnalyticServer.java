package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.MongoDBStorageConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnalyticServer extends Main implements IAnalyticServer, IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";

	public static final String APP_SETTINGS_DEFAULT_PATH = "server.analytic.settings";
	public static final String APP_DUMP_DEFAULT_PATH = "server.analytic.dump";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	private UUID a_oIdentifier = null;
	private UUID a_iSettings = null;
	private boolean a_bIsIdle = true;

	private Queue<Serializable> a_lDataQueue = null; // TODO IData
	private Queue<Serializable> a_lActionQueue = null; // TODO IAction
	private Queue<Serializable> a_lResultQueue = null; // TODO IData
	private Queue<Serializable> a_lConfigureConnectionQueue = null; // TODO IConfigureConnection

	// TODO fix coding violation use of complex language property/feature (anonymous classes).
	private Map<UUID, IConnectionConstructor> a_lConnectionConstructors = null;

	protected AnalyticServer() {
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
		a_lResultQueue = new ConcurrentLinkedQueue<>();
		a_lConfigureConnectionQueue = new ConcurrentLinkedQueue<>();
		a_lConnectionConstructors = new ConcurrentHashMap<>();

		/* temporary configuration begin */
		Fx_RegisterConnectionConstructors();
		/* temporary configuration end */

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private void Fx_RegisterConnectionConstructors() {
		// TODO violation coding use of lambda function
		ISettings oSettings = a_mSettingsManager.GetSettings(a_iSettings);
		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("action.control.producer.type")),
				oConfiguration -> {
					IConnection oConnection = new RabbitMQProducerConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetFromQueue(a_lActionQueue);
					return oConnection;
				});

		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("data.realtime.consumer.type")),
				oConfiguration -> {
					IConnection oConnection = new RabbitMQConsumerConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetToQueue(a_lDataQueue);
					return oConnection;
				});

		a_lConnectionConstructors.put(UUID.fromString(oSettings.GetString("data.result.producer.type")),
				oConfiguration -> {
					IConnection oConnection = new MongoDBStorageConnection(oConfiguration.GetIdentifier());
					oConnection.Configure(Fx_CreateConnectionSettings(oConfiguration));
					oConnection.SetFromQueue(a_lResultQueue);
					return oConnection;
				});
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
		Fx_EstablishConnection(oConstructor.New(oConfigure));
		a_bIsIdle = false;
	}

	private void Fx_Analyze() {
		Serializable ptrData = a_lDataQueue.poll();
		if(ptrData == null) {
			return;
		}
		IData oData = (IData) ptrData;
		// TODO stub implementation
		//IAction oAction = Generator.GenerateActionSensor(null);
		IAction oAction = Generator.GenerateActionMachine(null);
		UUID[] lActions = new UUID[1];
		lActions[0] = oAction.GetIdentifier();
		int nTuple = 1;
		IData oResult = Generator.GenerateResult(oData.GetIdentifier(),nTuple,lActions);
		a_lActionQueue.add(oAction);
		a_lResultQueue.add(oResult);
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
			Fx_ConfigureConnection();
			Fx_Analyze();
			Exit();
		}
	}

	public static void main(String[] arguments)
	{
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
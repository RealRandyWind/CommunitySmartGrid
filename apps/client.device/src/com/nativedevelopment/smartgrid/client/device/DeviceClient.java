package com.nativedevelopment.smartgrid.client.device;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;

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
	private boolean a_bIsIdle = true;

	private Queue<Serializable> a_lDataQueue = null; // TODO IData
	private Queue<Serializable> a_lActionQueue = null; // TODO IAction
	private Queue<Serializable> a_lConfigureConnectionQueue = null; // TODO IConfigureConnection
	private Map<UUID, Serializable> a_lActionMap = null; // TODO iAction -> iInstruction

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
		Configure(oDeviceClientSettings);

		/* temporary configuration begin */

		/* temporary configuration end */

		a_lDataQueue = new ConcurrentLinkedQueue<>();
		a_lActionQueue = new ConcurrentLinkedQueue<>();
		a_lConfigureConnectionQueue = new ConcurrentLinkedQueue<>();
		a_lActionMap = new ConcurrentHashMap<>(); // TODO maybe not concurrent actions are fixed

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new DeviceClient();
		return a_oInstance;
	}

	private UUID Fx_EstablishRealTimeDataConnection(UUID iConnection) {
		IConnection oConnection = new RabbitMQProducerConnection(iConnection);
		oConnection.SetFromQueue(a_lDataQueue);
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	private UUID Fx_EstablishActionControlConnection(UUID iConnection) {
		IConnection oConnection = new RabbitMQConsumerConnection(iConnection);
		oConnection.SetToQueue(a_lActionQueue);
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		if(oConnection == null) {
			a_mLogManager.Warning("attempt of a null connection",0);
			return null;
		}
		// TODO configure connections
		//oConnection.Configure(oSettings);
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private void Fx_ConfigureConnection() {
		Serializable oSerializable =  a_lConfigureConnectionQueue.poll();
		if(oSerializable == null) {
			return;
		}
		IConfigureConnection oSettingsMap = (IConfigureConnection) oSerializable;
		a_mLogManager.Warning("not yet implemented",0);
		a_bIsIdle = false;
	}

	private void Fx_PerformAction() {
		Serializable oSerializable = a_lActionQueue.poll();
		if(oSerializable == null) {
			return;
		}
		IAction oAction = (IAction) oSerializable;
		// TODO invoke at main loop
		a_mLogManager.Warning("not yet implemented",0);
		a_bIsIdle = false;
	}

	private void Fx_ProduceData() {
		IData oData = null;
		// TODO invoke at main loop
		a_mLogManager.Warning("not yet implemented",0);
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
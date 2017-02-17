package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.MongoDBStorageConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnalyticServer extends Main implements IAnalyticServer, IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";

	public static final String APP_SETTINGS_DEFAULT_PATH = "server.analytic.settings";
	public static final String APP_SETTINGS_RECOVERY_DEFAULT_PATH = "server.analytic.recovery.dump";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	private UUID a_oIdentifier = null;
	private boolean a_bIsIdle = true;

	private Queue<Serializable> a_lDataQueue = null; // TODO IData
	private Queue<Serializable> a_lActionQueue = null; // TODO IAction
	private Queue<Serializable> a_lResultQueue = null; // TODO IData
	private Queue<Serializable> a_lConfigureConnectionQueue = null; // TODO IConfigureConnection

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
		Configure(oDeviceClientSettings);

		/* temporary configuration begin */

		/* temporary configuration end */

		a_lDataQueue = new ConcurrentLinkedQueue<>();
		a_lActionQueue = new ConcurrentLinkedQueue<>();
		a_lResultQueue = new ConcurrentLinkedQueue<>();
		a_lConfigureConnectionQueue = new ConcurrentLinkedQueue<>();

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

	public UUID EstablishActionControlConnection(IConfigureConnection oConfigure) {
		IConnection oConnection = new RabbitMQProducerConnection(oConfigure.GetIdentifier());
		oConnection.SetFromQueue(a_lActionQueue);
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishRealtimeDataConnection(IConfigureConnection oConfigure) {
		IConnection oConnection = new RabbitMQConsumerConnection(oConfigure.GetIdentifier());
		oConnection.SetToQueue(a_lDataQueue);
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishResultsConnection(IConfigureConnection oConfigure) {
		IConnection oConnection = new MongoDBStorageConnection(oConfigure.GetIdentifier());
		oConnection.SetFromQueue(a_lResultQueue);
		a_mLogManager.Warning("not yet implemented",0); // TODO MongoDBConnection
		return Fx_EstablishConnection(oConnection);
	}

	private void Fx_ConfigureConnection() {
		Serializable ptrConfigureConnection =  a_lConfigureConnectionQueue.poll();
		if(ptrConfigureConnection == null) {
			return;
		}
		IConfigureConnection oConfigureConnection = (IConfigureConnection) ptrConfigureConnection;
		a_mLogManager.Warning("not yet implemented",0);
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
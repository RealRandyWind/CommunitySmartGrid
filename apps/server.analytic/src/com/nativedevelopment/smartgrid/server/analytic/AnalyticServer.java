package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.connection.MongoDBStorageConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnalyticServer extends Main implements IAnalyticServer, IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String APP_SETTINGS_DEFAULT_PATH = "server.analytic.settings";
	public static final String APP_DUMP_DEFAULT_PATH = "server.analytic.dump";
	public static final String APP_CONNECTION_ACTIONCONTROLPRODUCER_PREFIX = "action.control.producer.";
	public static final String APP_CONNECTION_DATAREAILTIMECONSUMER_PREFIX = "data.realtime.consumer.";
	public static final String APP_CONNECTION_RESULTSTORE_PREFIX = "result.store.";
	public static final String APP_CONNECTION_STATUSMONITORPRODUCER_PREFIX = "status.monitor.producer.";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private RabbitMQConsumerConnection a_oDataRealtimeConsumer = null;
	private RabbitMQProducerConnection a_oActionControlProducer = null;
	private MongoDBStorageConnection a_oResultStore = null;
	private UDPProducerConnection a_oStatusMonitorProducer = null;

	private UUID a_oIdentifier = null;
	private UUID a_iSettings = null;
	private boolean a_bIsIdle = true;

	private TimeOut a_oTimeOut = null;

	private Queue<Serializable> a_lDataQueue = null; // TODO IData
	private Queue<Serializable> a_lActionQueue = null; // TODO IAction
	private Queue<Serializable> a_lResultQueue = null; // TODO IData
	private Queue<Serializable> a_lStatusQueue = null; // TODO IStatus
	private Queue<Serializable> a_lObserverQueue = null; // TODO IAddress

	protected AnalyticServer() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_oTimeOut = new TimeOut();
	}

	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	public void ShutDown() {
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		a_oDataRealtimeConsumer.Close();
		a_oActionControlProducer.Close();
		//a_oResultStore.Close();
		a_oStatusMonitorProducer.Close();

		// TODO join

		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();

		ISettings oAnalyticServerSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		a_iSettings = oAnalyticServerSettings.GetIdentifier();
		Configure(oAnalyticServerSettings);

		a_lDataQueue = new ConcurrentLinkedQueue<>();
		a_lActionQueue = new ConcurrentLinkedQueue<>();
		a_lResultQueue = new ConcurrentLinkedQueue<>();
		a_lStatusQueue = new ConcurrentLinkedQueue<>();
		a_lObserverQueue = new ConcurrentLinkedQueue<>();

		/* temporary configuration begin */
		a_oDataRealtimeConsumer = new RabbitMQConsumerConnection(null);
		oAnalyticServerSettings.SetKeyPrefix(APP_CONNECTION_DATAREAILTIMECONSUMER_PREFIX);
		a_oDataRealtimeConsumer.SetToQueue(a_lDataQueue);
		a_oDataRealtimeConsumer.Configure(oAnalyticServerSettings);

		a_oActionControlProducer = new RabbitMQProducerConnection(null);
		oAnalyticServerSettings.SetKeyPrefix(APP_CONNECTION_ACTIONCONTROLPRODUCER_PREFIX);
		a_oActionControlProducer.SetFromQueue(a_lActionQueue);
		a_oActionControlProducer.Configure(oAnalyticServerSettings);

		/*
		a_oResultStore = new MongoDBStorageConnection(null);
		oAnalyticServerSettings.SetKeyPrefix(APP_CONNECTION_RESULTSTORE_PREFIX);
		a_oResultStore.SetFromQueue(a_lResultQueue);
		a_oResultStore.Configure(oAnalyticServerSettings);
		*/

		a_oStatusMonitorProducer = new UDPProducerConnection(null);
		oAnalyticServerSettings.SetKeyPrefix(APP_CONNECTION_STATUSMONITORPRODUCER_PREFIX);
		a_oStatusMonitorProducer.SetFromQueue(a_lStatusQueue);
		a_oStatusMonitorProducer.SetRemoteQueue(a_lObserverQueue);
		a_oStatusMonitorProducer.Configure(oAnalyticServerSettings);

		oAnalyticServerSettings.SetKeyPrefix("");
		a_oDataRealtimeConsumer.Open();
		a_oActionControlProducer.Open();
		//a_oResultStore.Open();
		a_oStatusMonitorProducer.Open();

		/* temporary configuration end */

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	private void Fx_Analyze() {
		Serializable ptrData = a_lDataQueue.poll();
		if(ptrData == null) { return; }
		IData oData = (IData) ptrData;
		IAction oAction = Generator.GenerateActionMachine(null);
		IPackage oPackage = new Package(oAction,oData.GetIdentifier(),null,0,System.currentTimeMillis());
		int nTuple = 1;
		UUID[] lActions = new UUID[1];
		lActions[0] = oAction.GetIdentifier();
		IData oResult = Generator.GenerateResult(oData.GetIdentifier(),nTuple,lActions);
		a_lActionQueue.offer(oPackage);
		a_lResultQueue.offer(oResult);
		a_bIsIdle = false;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable oIdentifier = oConfigurations.Get(SETTINGS_KEY_IDENTIFIER);
		a_oIdentifier = oIdentifier == null ? UUID.randomUUID() : UUID.fromString((String)oIdentifier);
		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
		a_oTimeOut.SetUpperBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND));
		a_oTimeOut.SetDelta((int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND));
		a_mLogManager.Success("configured",0);
	}

	@Override
	public void Run() {
		try {
			while(!IsClosing()) {
				a_oTimeOut.Routine(a_bIsIdle);
				a_bIsIdle = true;
				Fx_Analyze();
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			a_mLogManager.Warning("%s \"%s\"\n",0
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}

	public static void main(String[] arguments) {
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
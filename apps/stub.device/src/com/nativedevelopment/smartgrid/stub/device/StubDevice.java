package com.nativedevelopment.smartgrid.stub.device;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connections.TCPConsumerConnection;
import com.nativedevelopment.smartgrid.connections.TCPProducerConnection;
import com.nativedevelopment.smartgrid.connections.UDPConsumerConnection;
import com.nativedevelopment.smartgrid.connections.UDPProducerConnection;

import java.io.Serializable;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StubDevice extends Main implements IConfigurable {
	public static final String APP_UDP = "UDP";
	public static final String APP_TCP = "TCP";

	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String APP_SETTINGS_DEFAULT_PATH = "stub.device.settings";
	public static final String APP_DUMP_DEFAULT_PATH = "stub.device.dump";
	public static final String APP_CONNECTION_DATAREALTIMEPRODUCERUDP_PREFIX = "data.realtime.producer.udp.";
	public static final String APP_CONNECTION_ACTIONCONTROLCONSUMERUDP_PREFIX = "action.control.consumer.udp.";
	public static final String APP_CONNECTION_DATAREALTIMEPRODUCERTCP_PREFIX = "data.realtime.producer.tcp.";
	public static final String APP_CONNECTION_ACTIONCONTROLCONSUMERTCP_PREFIX = "action.control.consumer.tcp.";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;

	private IConnection a_oDataRealtimeProducer = null;
	private IConnection a_oActionControlConsumer = null;

	private UUID a_oIdentifier = null;
	private boolean a_bIsIdle = true;
	private TimeOut a_oTimeOut = null;

	private Deque<Serializable> a_lDataQueue = null; // TODO IData
	private Deque<Serializable> a_lActionQueue = null; // TODO IAction
	private Map<UUID, Serializable> a_lActionMap = null; // TODO iAction -> IInstruction

	private boolean a_bIsMachine = true;
	private boolean a_bIsUDP = true;
	private String a_sConnectionTypeName = null;

	protected StubDevice() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_oTimeOut = new TimeOut();
		a_bIsMachine = Generator.NextBoolean();
		a_bIsUDP = Generator.NextBoolean();
		a_sConnectionTypeName = a_bIsUDP ? APP_UDP : APP_TCP;
	}

	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	public void ShutDown() {
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		a_oActionControlConsumer.Close();
		a_oDataRealtimeProducer.Close();
		// TODO join

		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();

		a_lDataQueue = new ConcurrentLinkedDeque<>();
		a_lActionQueue = new ConcurrentLinkedDeque<>();
		a_lActionMap = new ConcurrentHashMap<>();

		ISettings oDeviceClientSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		oDeviceClientSettings.GetIdentifier();
		Configure(oDeviceClientSettings);

		if(a_bIsUDP) {
			UDPProducerConnection oDataRealtimeProducer = new UDPProducerConnection(null);
			oDeviceClientSettings.SetKeyPrefix(APP_CONNECTION_DATAREALTIMEPRODUCERTCP_PREFIX);
			oDataRealtimeProducer.SetFromQueue(a_lDataQueue);
			oDataRealtimeProducer.Configure(oDeviceClientSettings);

			UDPConsumerConnection oActionControlConsumer = new UDPConsumerConnection(null);
			oDeviceClientSettings.SetKeyPrefix(APP_CONNECTION_ACTIONCONTROLCONSUMERTCP_PREFIX);
			oActionControlConsumer.SetToQueue(a_lActionQueue);
			oActionControlConsumer.Configure(oDeviceClientSettings);
			oActionControlConsumer.SetRoutingKey(GetIdentifier().toString());

			a_oDataRealtimeProducer = oDataRealtimeProducer;
			a_oActionControlConsumer = oActionControlConsumer;
		} else {
			TCPProducerConnection oDataRealtimeProducer = new TCPProducerConnection(null);
			oDeviceClientSettings.SetKeyPrefix(APP_CONNECTION_DATAREALTIMEPRODUCERTCP_PREFIX);
			oDataRealtimeProducer.Configure(oDeviceClientSettings);

			TCPConsumerConnection oActionControlConsumer = new TCPConsumerConnection(null);
			oDeviceClientSettings.SetKeyPrefix(APP_CONNECTION_ACTIONCONTROLCONSUMERTCP_PREFIX);
			oActionControlConsumer.Configure(oDeviceClientSettings);
			oActionControlConsumer.SetRoutingKey(GetIdentifier().toString());

			a_oDataRealtimeProducer = oDataRealtimeProducer;
			a_oActionControlConsumer = oActionControlConsumer;
		}

		oDeviceClientSettings.SetKeyPrefix("");
		a_oDataRealtimeProducer.Open();
		a_oActionControlConsumer.Open();

		a_mLogManager.Info("data.realtime.producer (%s) \"%s\"",0, a_sConnectionTypeName
				,a_oDataRealtimeProducer.GetIdentifier().toString());
		a_mLogManager.Info("action.control.consumer (%s) \"%s\"",0, a_sConnectionTypeName
				,a_oActionControlConsumer.GetIdentifier().toString());

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new StubDevice();
		return a_oInstance;
	}

	private void Fx_PerformAction() {
		Serializable ptrAction = a_lActionQueue.pollFirst();
		if(ptrAction == null) {
			return;
		}
		IAction oAction = (IAction) ptrAction;
		a_mLogManager.Log("perform action %s by %s",0,oAction.GetIdentifier().toString(),GetIdentifier().toString());
		// TODO sub implementation
		a_bIsIdle = false;
	}

	private void Fx_ProduceData() {
		int nTuples = 1;
		IData oData = Generator.GenerateDataSensor(a_oIdentifier, nTuples);
		a_mLogManager.Log("produce data by %s",0,GetIdentifier().toString());
		a_lDataQueue.offerLast(oData);
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
				Fx_ProduceData();
				Fx_PerformAction();
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			a_mLogManager.Warning("@%s %s \"%s\"\n",0
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}

	public static void main(String[] arguments) {
		Main oApplication = StubDevice.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
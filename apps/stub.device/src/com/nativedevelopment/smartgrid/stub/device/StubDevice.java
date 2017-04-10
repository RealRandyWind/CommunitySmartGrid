package com.nativedevelopment.smartgrid.stub.device;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connections.TCPConsumerConnection;
import com.nativedevelopment.smartgrid.connections.TCPProducerConnection;
import com.nativedevelopment.smartgrid.connections.UDPConsumerConnection;
import com.nativedevelopment.smartgrid.connections.UDPProducerConnection;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StubDevice extends Main implements IConfigurable {
	public static final String APP_UDP = "UDP";
	public static final String APP_TCP = "TCP";
	public static final String APP_MACHINE = "Machine";
	public static final String APP_SENSOR = "Sensor";

	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";

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

	private Deque<Serializable> a_lRemoteQueue = null;
	private Deque<Serializable> a_lDataQueue = null;
	private Deque<Serializable> a_lActionQueue = null;
	private Map<UUID, Serializable> a_lActionMap = null;

	private boolean a_bIsMachine = true;
	private boolean a_bIsUDP = true;
	private String a_sConnectionTypeName = null;
	private String a_sDeviceTypeName = null;

	private StubDevice() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_oTimeOut = new TimeOut();
		a_bIsMachine = Generator.NextBoolean();
		a_bIsUDP = Generator.NextBoolean();
		a_sConnectionTypeName = a_bIsUDP ? APP_UDP : APP_TCP;
		a_sDeviceTypeName = a_bIsMachine ? APP_MACHINE : APP_SENSOR;
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

		a_lRemoteQueue = new ConcurrentLinkedDeque<>();
		a_lDataQueue = new ConcurrentLinkedDeque<>();
		a_lActionQueue = new ConcurrentLinkedDeque<>();
		a_lActionMap = new ConcurrentHashMap<>();

		ISettings oStubDeviceSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		oStubDeviceSettings.GetIdentifier();
		Configure(oStubDeviceSettings);

		String sRemote = "localhost";
		int iPort = 0;
		if(a_bIsUDP) {
			UDPProducerConnection oDataRealtimeProducer = new UDPProducerConnection(null);
			oStubDeviceSettings.SetKeyPrefix(APP_CONNECTION_DATAREALTIMEPRODUCERUDP_PREFIX);
			oDataRealtimeProducer.SetRemoteQueue(a_lRemoteQueue);
			oDataRealtimeProducer.SetFromQueue(a_lDataQueue);
			oDataRealtimeProducer.Configure(oStubDeviceSettings);
			sRemote = oStubDeviceSettings.GetString(SETTINGS_KEY_REMOTEADDRESS);
			iPort = (int)oStubDeviceSettings.Get(SETTINGS_KEY_REMOTEPORT);

			UDPConsumerConnection oActionControlConsumer = new UDPConsumerConnection(null);
			oStubDeviceSettings.SetKeyPrefix(APP_CONNECTION_ACTIONCONTROLCONSUMERUDP_PREFIX);
			oActionControlConsumer.SetToQueue(a_lActionQueue);
			oActionControlConsumer.Configure(oStubDeviceSettings);
			oActionControlConsumer.SetRoutingKey(GetIdentifier().toString());

			a_oDataRealtimeProducer = oDataRealtimeProducer;
			a_oActionControlConsumer = oActionControlConsumer;
		} else {
			TCPProducerConnection oDataRealtimeProducer = new TCPProducerConnection(null);
			oStubDeviceSettings.SetKeyPrefix(APP_CONNECTION_DATAREALTIMEPRODUCERTCP_PREFIX);
			oDataRealtimeProducer.SetRemoteQueue(a_lRemoteQueue);
			oDataRealtimeProducer.SetFromQueue(a_lDataQueue);
			oDataRealtimeProducer.Configure(oStubDeviceSettings);
			sRemote = oStubDeviceSettings.GetString(SETTINGS_KEY_REMOTEADDRESS);
			iPort = (int)oStubDeviceSettings.Get(SETTINGS_KEY_REMOTEPORT);

			TCPConsumerConnection oActionControlConsumer = new TCPConsumerConnection(null);
			oStubDeviceSettings.SetKeyPrefix(APP_CONNECTION_ACTIONCONTROLCONSUMERTCP_PREFIX);
			oActionControlConsumer.SetToQueue(a_lActionQueue);
			oActionControlConsumer.Configure(oStubDeviceSettings);
			oActionControlConsumer.SetRoutingKey(GetIdentifier().toString());

			a_oDataRealtimeProducer = oDataRealtimeProducer;
			a_oActionControlConsumer = oActionControlConsumer;
		}

		a_lRemoteQueue.offerLast(new InetSocketAddress(sRemote, iPort));

		oStubDeviceSettings.SetKeyPrefix("");
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
		IData oData;
		if(a_bIsMachine) {
			oData = Generator.GenerateDataMachine(a_oIdentifier, nTuples);
		} else {
			oData = Generator.GenerateDataSensor(a_oIdentifier, nTuples);
		}
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
		a_mLogManager.Info("device running (%s) \"%s\"",0, a_sDeviceTypeName, GetIdentifier().toString());
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
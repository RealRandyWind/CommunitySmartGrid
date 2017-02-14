package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Main {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_LOGFILE = "log.file";

	public static final String SETTINGS_KEYPREFIX_DATAREALTIME = "data.realtime.";
	public static final String SETTINGS_KEYPREFIX_ACTIONCONTROLL = "action.control.";
	public static final String SETTINGS_KEYPREFIX_MONITORING = "monitoring.";
	public static final String SETTINGS_KEYPREFIX_BROADCAST = "broadcast.";

	private static final String APP_SETTINGS_DEFAULT_PATH = "client.settings";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	private Map<UUID, IDevice> a_lDevices = null;
	private UUID a_oIdentifier = null;

	private Queue<Serializable> a_lDeviceData = null;  // TODO IData
	private Queue<Serializable> a_lDeviceActions = null; // TODO IAction
	private Queue<Serializable> a_lActions = null;  // TODO IAction

	protected Client() {
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

		a_lDevices = new HashMap<>();
		a_lDeviceActions = new ConcurrentLinkedQueue<>();
		a_lDeviceData = new ConcurrentLinkedQueue<>();
		a_lActions = new ConcurrentLinkedQueue<>();

		/* configure client start */
		ISettings lClientSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);

		a_mLogManager.SetLogFile(lClientSettings.GetString(SETTINGS_KEY_LOGFILE));
		a_oIdentifier = UUID.fromString(lClientSettings.GetString(SETTINGS_KEY_IDENTIFIER));

		Fx_EstablishMainConnection(null);
		/* configure client end */

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new Client();
		return a_oInstance;
	}

	private UUID Fx_EstablishMainConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
		return oConnection.GetIdentifier();
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		oConnection.SetToLogQueue(a_mLogManager.GetLogQueue());
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private IDevice Fx_CompileDevice(String sSource) {
		return null;
	}

	public IDevice RegisterDeviceFromFile(String sPath) {
		String sSource = null;
		//TODO load file to string
		return RegisterDeviceFromString(sSource);
	}

	public IDevice RegisterDeviceFromString(String sSource) {
		IDevice oDevice = Fx_CompileDevice(sSource);
		RegisterDevice(oDevice);
		return  oDevice;
	}

	public void RegisterDevice(IDevice oDevice) {
		a_lDevices.put(oDevice.GetIdentifier(), oDevice);
	}

	public void UnregisterDevice(UUID iDevice) {
		a_lDevices.remove(iDevice);
	}

	public IDevice GetDevice(UUID iDevice) {
		return a_lDevices.get(iDevice);
	}

	public Iterable<IDevice> GetAllDevices() {
		return a_lDevices.values();
	}

	public static void main(String[] arguments)
	{
		Main oApplication = Client.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
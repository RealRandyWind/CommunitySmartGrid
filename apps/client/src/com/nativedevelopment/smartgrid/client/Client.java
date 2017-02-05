package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.*;

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

	private Queue<IData> a_lDeviceData = null;
	private Queue<IAction> a_lDeviceActions = null;
	private Queue<IAction> a_lActions = null;

	protected Client() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_mConnectionManager = MConnectionManager.GetInstance();
	}

	public void ShutDown() {
		a_mConnectionManager.ShutDown();
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [Client.ShutDown]\n");
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

		// TODO subscription validation token
		// TODO establish main connection
		// TODO establish action control connection
		// TODO establish (await) dens data connection (request)
		// TODO establish (await) realtime data connection
		// TODO setup device listeners
		// TODO establish device connections
		/* configure client end */

		a_mLogManager.Success("[Client.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new Client();
		return a_oInstance;
	}

	private UUID Fx_EstablishMainConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.Fx_EstablishMainConnection] not yet implemented",0);
		return oConnection.GetIdentifier();
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private UUID Fx_EstablishSubscriptionConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.Fx_EstablishSubscriptionConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private UUID Fx_EstablishMonitoringConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.Fx_EstablishMonitoringConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
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

	public UUID EstablishActionControlConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.EstablishActionControlConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishDensDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.EstablishDensDataConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishRealtimeDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[Client.EstablishRealtimeDataConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishControllerConnection(UUID iConnection) {
		IConnection oConnection = null;
		a_mLogManager.Warning("[Client.EstablishControllerConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishSubscriptionConnection(UUID iConnection) {
		IConnection oConnection = null;
		a_mLogManager.Warning("[Client.EstablishSubscriptionConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishDeviceConnection(UUID iConnection) {
		IConnection oConnection = null;
		a_mLogManager.Warning("[Client.EstablishDeviceConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public static void main(String[] arguments)
	{
		Main oApplication = Client.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
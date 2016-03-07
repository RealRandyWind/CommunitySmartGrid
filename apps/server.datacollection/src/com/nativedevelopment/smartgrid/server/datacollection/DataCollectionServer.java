package com.nativedevelopment.smartgrid.server.datacollection;

import com.nativedevelopment.smartgrid.*;

import java.util.Map;
import java.util.UUID;

public class DataCollectionServer extends Main {
	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	private UUID a_iConnection = null;
	private UUID a_oIdentifier = null;

	protected DataCollectionServer() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_mConnectionManager = MConnectionManager.GetInstance();
	}

	public void ShutDown() {
		a_mConnectionManager.ShutDown();
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [DataCollectionServer.ShutDown]\n");
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();
		a_mConnectionManager.SetUp();

		a_mLogManager.Success("[DataCollectionServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new DataCollectionServer();
		return a_oInstance;
	}

	private UUID Fx_EstablishMainConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[DataCollectionServer.Fx_EstablishMainConnection] not yet implemented",0);
		return oConnection.GetIdentifier();
	}

	private UUID Fx_EstablishConnection(IConnection oConnection) {
		a_mConnectionManager.AddConnection(oConnection);
		return oConnection.GetIdentifier();
	}

	private void Fx_TerminateConnection(UUID iConnection) {
		a_mConnectionManager.RemoveConnection(iConnection);
	}

	private UUID Fx_EstablishMonitoringConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[DataCollectionServer.Fx_EstablishMonitoringConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishActionControlConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[DataCollectionServer.EstablishActionControlConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishDensDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[DataCollectionServer.EstablishDensDataConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishRealtimeDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("[DataCollectionServer.EstablishRealtimeDataConnection] not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public static void main(String[] arguments)
	{
		Main oApplication = DataCollectionServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
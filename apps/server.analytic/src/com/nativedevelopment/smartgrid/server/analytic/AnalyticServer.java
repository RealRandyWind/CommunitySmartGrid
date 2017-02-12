package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.*;

import java.util.UUID;

public class AnalyticServer extends Main {
	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

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

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	private UUID Fx_EstablishMainConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
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
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishActionControlConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishDensDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishRealtimeDataConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public UUID EstablishResultsConnection(UUID iConnection) {
		IConnection oConnection = null;
		// TODO
		a_mLogManager.Warning("not yet implemented",0);
		return Fx_EstablishConnection(oConnection);
	}

	public static void main(String[] arguments)
	{
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
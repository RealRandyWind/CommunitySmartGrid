package com.nativedevelopment.smartgrid.server.message;

import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.MSettingsManager;
import com.nativedevelopment.smartgrid.MConnectionManager;

public class MessageServer extends Main {
	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;
	private MConnectionManager a_mConnectionManager = null;

	protected MessageServer() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_mConnectionManager = MConnectionManager.GetInstance();
	}

	public void ShutDown() {
		a_mConnectionManager.ShutDown();
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [MessageServer.ShutDown]\n");
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();
		a_mConnectionManager.SetUp();

		a_mLogManager.Success("[MessageServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MessageServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = MessageServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

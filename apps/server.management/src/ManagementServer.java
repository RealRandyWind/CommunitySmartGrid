package com.nativedevelopment.smartgrid.server.management;

import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;

public class ManagementServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();

	protected ManagementServer() {

	}

	public void ShutDown() {
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [ManagementServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();

		mLogManager.Success("[ManagementServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new ManagementServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = ManagementServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

package com.nativedevelopment.smartgrid.server.datacollection;

import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.MConnectionManager;

public class DataCollectionServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	
	protected DataCollectionServer() {

	}

	protected void ShutDown() {
		mConnectionMannager.ShutDown();
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [DataCollectionServer.ShutDown]\n");
	}

	protected void SetUp() {
		mLogManager.SetUp();
		mConnectionMannager.SetUp();

		mLogManager.Success("[DataCollectionServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new DataCollectionServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = DataCollectionServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

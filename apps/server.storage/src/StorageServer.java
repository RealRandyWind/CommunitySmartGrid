package com.nativedevelopment.smartgrid.server.storage;

import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;

public class StorageServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();

	protected StorageServer() {

	}

	public void ShutDown() {
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [StorageServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();

		mLogManager.Success("[StorageServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new StorageServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = StorageServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

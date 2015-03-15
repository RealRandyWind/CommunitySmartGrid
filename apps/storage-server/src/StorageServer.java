package com.nativedevelopment.smartgrid.server.storage;

import com.nativedevelopment.smartgrid.Main;

public class StorageServer extends Main {
	protected StorageServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [StorageServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [StorageServer.StartUp]\n");
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

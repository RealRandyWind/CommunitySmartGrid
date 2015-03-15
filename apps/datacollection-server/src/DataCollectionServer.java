package com.nativedevelopment.smartgrid.server.datacollection;

import com.nativedevelopment.smartgrid.Main;

public class DataCollectionServer extends Main {
	protected DataCollectionServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [DataCollectionServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [DataCollectionServer.StartUp]\n");
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

package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.Main;

public class Client extends Main {
	protected Client() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [Client.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [Client.StartUp]\n");
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new Client();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = Client.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

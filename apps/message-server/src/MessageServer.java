package com.nativedevelopment.smartgrid.server.message;

import com.nativedevelopment.smartgrid.Main;

public class MessageServer extends Main {
	protected MessageServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [MessageServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [MessageServer.StartUp]\n");
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

package com.nativedevelopment.smartgrid.server.subscription;

import com.nativedevelopment.smartgrid.Main;

public class SubscriptionServer extends Main {
	protected SubscriptionServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [SubscriptionServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [SubscriptionServer.StartUp]\n");
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new SubscriptionServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = SubscriptionServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

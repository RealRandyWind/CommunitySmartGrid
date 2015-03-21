package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.Main;

public class AnalyticServer extends Main {
	protected AnalyticServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [AnalyticServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [AnalyticServer.StartUp]\n");
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	public static void main(String[] arguments) {
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

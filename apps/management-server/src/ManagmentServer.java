package com.nativedevelopment.smartgrid.server.managment;

import com.nativedevelopment.smartgrid.Main;

public class ManagmentServer extends Main {
	protected ManagmentServer() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [ManagmentServer.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [ManagmentServer.StartUp]\n");
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new ManagmentServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = ManagmentServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

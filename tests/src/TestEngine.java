package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Main;

public class TestEngine extends Main {
	protected TestEngine() {

	}

	protected void ShutDown() {
		System.out.printf("_SUCCESS: [TestEngine.ShutDown]\n");
	}

	protected void StartUp() {
		System.out.printf("_SUCCESS: [TestEngine.StartUp]\n");
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new TestEngine();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = TestEngine.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Main;

public class TestEngine extends Main {
	protected TestEngine() {

	}

	public void ShutDown() {
		System.out.printf("_SUCCESS: [TestEngine.ShutDown]\n");
	}

	public void SetUp() {
		System.out.printf("_SUCCESS: [TestEngine.SetUp]\n");
	}

	public void Run() {
		System.out.printf("_SUCCESS: [TestEngine.Run]\n");
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

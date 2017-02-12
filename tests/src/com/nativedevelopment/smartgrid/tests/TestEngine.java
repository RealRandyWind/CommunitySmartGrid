package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Main;

import java.util.List;

public class TestEngine extends Main {
	private List<ITestCase> a_lTestsCases = null;

	protected TestEngine() {

	}

	public void ShutDown() {
		System.out.printf("_SUCCESS: %s\n", MLogManager.MethodName());
	}

	public void SetUp() {
		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void Run() {
		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
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

package com.nativedevelopment.smartgrid.tests;

public class Main {
	private static Main a_oInstance = null;
	private static int a_iExitCode = 0;
	private boolean a_bIsRunning = false;

	private Main() {
		a_bIsRunning = false;
	}

	private void Fx_ShutDown() {
		if(!a_bIsRunning) {
			System.out.printf("_WARNING: [Main.Entry] entry already not running\n");
		}

		// TODO Main ShutDown

		a_bIsRunning = false;
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new Main();
		return a_oInstance;
	}

	public static void ErrorShutDown() {
		Main.ErrorShutDown(1);
	}

	public static void ErrorShutDown(int iExitCode) {
		if(a_oInstance != null) { a_oInstance.Fx_ShutDown(); }
		a_iExitCode = (iExitCode == 0 ? iExitCode : 1);
		System.exit(a_iExitCode);
	}

	int Entry() {
		if(a_bIsRunning) {
			System.out.printf("_WARNING: [Main.Entry] entry already running\n");
			return 1;
		}
		a_bIsRunning = true;

		// TODO Code here

		Fx_ShutDown();
		return a_iExitCode;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = Main.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

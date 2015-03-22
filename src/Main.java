package com.nativedevelopment.smartgrid;

public class Main {
	protected static Main a_oInstance = null;
	private static int a_iExitCode = 0;
	private boolean a_bIsRunning = false;

	protected Main() {
		a_bIsRunning = false;
	}

	public void ShutDown() {
		System.out.printf("_WARNING: [Main.ShutDown] not yet implemented\n");
	}

	public void SetUp() {
		System.out.printf("_WARNING: [Main.SetUp] not yet implemented\n");
	}

	public void Run() {
		System.out.printf("_WARNING: [Main.Run] not yet implemented\n");
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

	public int Entry() {
		if(a_bIsRunning) {
			System.out.printf("_WARNING: [Main.Entry] already running\n");
			return 1;
		}
		a_bIsRunning = true;

		SetUp();
		Run();
		Fx_ShutDown();

		return a_iExitCode;
	}

	private void Fx_ShutDown() {
		if(!a_bIsRunning) {
			System.out.printf("_WARNING: [Main.Entry] already not running\n");
		}

		ShutDown();

		a_bIsRunning = false;
	}
}

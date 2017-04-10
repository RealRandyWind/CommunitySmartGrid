package com.nativedevelopment.smartgrid.stub.server.analytic.gui;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connections.RMIControllerCallerConnection;
import com.nativedevelopment.smartgrid.controllers.interfaces.IAnalyticServerController;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

public class StubServerAnalyticGUI extends Main implements IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String APP_SETTINGS_DEFAULT_PATH = "stub.server.analytic.gui.settings";
	public static final String APP_DUMP_DEFAULT_PATH = "stub.server.analytic.gui.dump";
	public static final String APP_CONNECTION_CONTROLLERCALLER_PREFIX = "controller.caller.";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;

	private RMIControllerCallerConnection a_oControllerCaller = null;

	private IPromise a_oPromise = null;
	private IAnalyticServerController a_oControllerStub = null;

	private UUID a_oIdentifier = null;
	private boolean a_bIsIdle = true;
	private TimeOut a_oTimeOut = null;
	private String a_sExchange = null;

	private StubServerAnalyticGUI() {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();
		a_oTimeOut = new TimeOut();
	}

	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	public void ShutDown() {
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();

		a_oControllerCaller.Close();

		// TODO join

		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();

		ISettings oStubServerAnalyticGUISettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		oStubServerAnalyticGUISettings.GetIdentifier();
		Configure(oStubServerAnalyticGUISettings);

		a_oPromise = new Promise();
		a_oControllerCaller = new RMIControllerCallerConnection(null);
		oStubServerAnalyticGUISettings.SetKeyPrefix(APP_CONNECTION_CONTROLLERCALLER_PREFIX);
		a_oControllerCaller.Configure(oStubServerAnalyticGUISettings);
		a_oControllerCaller.SetPromise(a_oPromise);

		oStubServerAnalyticGUISettings.SetKeyPrefix("");
		a_oControllerCaller.Open();

		a_mLogManager.Info("controller.caller (RMI) \"%s\"",0,a_oControllerCaller.GetIdentifier().toString());

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new StubServerAnalyticGUI();
		return a_oInstance;
	}

	private void Fx_Control() throws Exception {
		if(!a_oPromise.IsDone()) { return; }
		a_oControllerStub = (IAnalyticServerController) a_oPromise.Get();
		a_mLogManager.Log("\"%s\" %s", 0
				,GetIdentifier()
				,a_oControllerStub.Notify(GetIdentifier().toString()));
		a_oTimeOut.Now();
		a_bIsIdle = false;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable oIdentifier = oConfigurations.Get(SETTINGS_KEY_IDENTIFIER);
		a_oIdentifier = oIdentifier == null ? UUID.randomUUID() : UUID.fromString((String)oIdentifier);
		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
		a_oTimeOut.SetUpperBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND));
		a_oTimeOut.SetDelta((int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND));
		a_mLogManager.Success("configured",0);
	}

	@Override
	public void Run() {
		try {
			while(!IsClosing()) {
				a_oTimeOut.Routine(a_bIsIdle);
				a_bIsIdle = true;
				Fx_Control();
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			a_mLogManager.Warning("@%s %s \"%s\"\n",0
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}

	public static void main(String[] arguments) {
		Main oApplication = StubServerAnalyticGUI.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}
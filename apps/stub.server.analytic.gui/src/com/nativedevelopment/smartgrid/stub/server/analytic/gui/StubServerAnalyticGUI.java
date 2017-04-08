package com.nativedevelopment.smartgrid.stub.server.analytic.gui;

import com.nativedevelopment.smartgrid.*;

import java.io.Serializable;
import java.util.UUID;

public class StubServerAnalyticGUI extends Main implements IConfigurable {
	public static final String SETTINGS_KEY_IDENTIFIER = "identifier";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String APP_SETTINGS_DEFAULT_PATH = "stub.server.analytic.gui.settings";
	public static final String APP_DUMP_DEFAULT_PATH = "stub.server.analytic.gui.dump";

	private MLogManager a_mLogManager = null;
	private MSettingsManager a_mSettingsManager = null;

	private UUID a_oIdentifier = null;
	private boolean a_bIsIdle = true;
	private TimeOut a_oTimeOut = null;

	protected StubServerAnalyticGUI() {
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

		// TODO join

		System.out.printf("_SUCCESS: %s\n",MLogManager.MethodName());
	}

	public void SetUp() {
		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();

		ISettings oDeviceClientSettings = a_mSettingsManager.LoadSettingsFromFile(APP_SETTINGS_DEFAULT_PATH);
		oDeviceClientSettings.GetIdentifier();
		Configure(oDeviceClientSettings);

		oDeviceClientSettings.SetKeyPrefix("");

		a_mLogManager.Success("",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new StubServerAnalyticGUI();
		return a_oInstance;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable oIdentifier = oConfigurations.Get(SETTINGS_KEY_IDENTIFIER);
		a_oIdentifier = oIdentifier == null ? UUID.randomUUID() : UUID.fromString((String)oIdentifier);
		oConfigurations.Set(SETTINGS_KEY_IDENTIFIER,oIdentifier.toString());
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
				Exit();
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
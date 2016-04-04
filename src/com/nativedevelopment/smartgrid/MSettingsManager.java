package com.nativedevelopment.smartgrid;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MSettingsManager {
    private static final String SETTING_ASSIGN_REGEX = "\\s*=\\s*";
    private static final int SETTING_KEYVALUE_LENGTH = 2;
    private static final int SETTING_ASSIGN_SPLIT_LIMIT = SETTING_KEYVALUE_LENGTH;
    private static final int SETTING_KEY = 0;
    private static final int SETTING_VALUE = 1;

    private static MSettingsManager a_oInstance = null;
    private boolean a_bIsSetUp = false;
    private boolean a_bIsShutDown = true;

    private MLogManager a_mLogManager = null;

    private Map<UUID,ISettings> a_lSettings = null;

    private MSettingsManager() {
        a_bIsShutDown = false;
        a_bIsSetUp = false;

        a_mLogManager = MLogManager.GetInstance();
    }

    public static MSettingsManager GetInstance() {
        if(a_oInstance != null) { return a_oInstance; }
        a_oInstance = new MSettingsManager();
        return a_oInstance;
    }

    public void SetUp() {
        if(a_bIsSetUp) {
            a_mLogManager.Warning("[MSettingsManager.SetUp] setup already.",0);
            return;
        }
        a_bIsShutDown = false;

        a_lSettings = new HashMap<>();

        a_mLogManager.Info("[MSettingsManager.SetUp] working directory \"%s\"",0,System.getProperty("user.dir"));

        a_bIsSetUp = true;
        a_mLogManager.Success("[MSettingsManager.SetUp]",0);
    }

    public void ShutDown() {
        if(a_bIsShutDown) {
            a_mLogManager.Warning("[MSettingsManager.ShutDown] shutdown already.",0);
            return;
        }
        a_bIsSetUp = false;

        // TODO MSettingsManager ShutDown

        a_bIsShutDown = true;
        a_mLogManager.Success("[MSettingsManager.ShutDown]",0);
    }

    private ISettings Fx_CompileSettings(String sSource) {
        ISettings oSettings = new Settings(null);
        String[] lLines = sSource.split(System.getProperty("line.separator"));
        for (String sLine : lLines) {
            String[] oKeyValue = sLine.split(SETTING_ASSIGN_REGEX, SETTING_ASSIGN_SPLIT_LIMIT);
            if(oKeyValue.length != SETTING_KEYVALUE_LENGTH) {
                a_mLogManager.Warning("[MSettingsManager.LoadSettingsFromFile] incorrect syntax in settings file.",0);
                continue;
            }
            oSettings.SetSpecial(oKeyValue[SETTING_KEY].toLowerCase(),oKeyValue[SETTING_VALUE]);
        }
        return oSettings;
    }

    public ISettings LoadSettingsFromFile(String sPath) {
        try {
            File oFile = new File(sPath);
			if(oFile.length() == 0) {
				a_mLogManager.Warning("[MSettingsManager.LoadSettingsFromFile] empty file \"%s\"",0,sPath);
			}
            FileInputStream oFileInputStream = new FileInputStream(oFile);
            // TODO fix may not be usable in arm java version
            byte[] rawSource = new byte[(int) oFile.length()];
            oFileInputStream.read(rawSource);
            String sSource = new String(rawSource, Charset.defaultCharset());
            return LoadSettingsFromString(sSource);
        } catch (Exception oException) {
            a_mLogManager.Warning("[MSettingsManager.LoadSettingsFromFile] %s \"%s\"",0
                    ,oException.getClass().getCanonicalName(),oException.getMessage());
        }
        return null;
    }

    public ISettings LoadSettingsFromString(String sSource) {
        ISettings oSettings = Fx_CompileSettings(sSource);
        AddSettings(oSettings);
        return oSettings;
    }

    public void AddSettings(ISettings oSettings) {
        a_lSettings.put(oSettings.GetIdentifier(), oSettings);
    }

    public ISettings GetSettings(UUID iSettings) {
        if (iSettings == null) {
            a_mLogManager.Warning("[MSettingsManager.GetSettings] null id.",0);
            return null;
        }
        return a_lSettings.get(iSettings);
    }

    public Iterable<ISettings> GetAllSettings() {
        return a_lSettings.values();
    }
}

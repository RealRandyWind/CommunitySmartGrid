package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings implements ISettings {
    protected static final String REGX_STRING = "\"((\\.|[^\\\"])*)\"";

    protected static Pattern a_oStringPattern =  Pattern.compile(REGX_STRING);

    private Map<String, Serializable> a_lSettings = null;
    private UUID a_oIdentifier = null;

    public Settings(UUID oIdentifier) {
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
        a_lSettings = new HashMap<>();
    }

    public UUID GetIdentifier() { return a_oIdentifier; }

    public String GetString(String sSetting) {
        return String.valueOf(Get(sSetting));
    }

    public Serializable Get(String sSetting) {
        return a_lSettings.get(sSetting.toLowerCase());
    }

    public void Set(String sSetting, Serializable oValue) {
        a_lSettings.put(sSetting.toLowerCase(), oValue);
    }

    public void SetSpecial(String sSetting, String sValue) {
        Matcher oMatcher = a_oStringPattern.matcher(sValue);
        System.out.printf("_WARNING: [Settings.SetSpecial] simple implementation.\n");
        Serializable oSetting = oMatcher.matches() ? oMatcher.group(1) : sValue;
        Set(sSetting, oSetting);
    }

    public Iterable<Map.Entry<String,Serializable>> GetAll() {
        return a_lSettings.entrySet();
    }
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings implements ISettings {
    protected static final String REGX_STRING = "\"((\\.|[^\"])*)\"\\s*";
    protected static final String REGX_INTEGER = "(-?\\d+)\\s*";
    protected static final String REGX_REAL = "(-?\\d*(.\\d|\\d.|\\d)\\d*)\\s*";
    protected static final String REGX_BOOLEAN = "(true|false)\\s*";
    protected static final String REGX_NULL = "(null)\\s*";
    private static final int MATCH_GROUP = 1;

    protected static final Pattern a_oStringPattern =  Pattern.compile(REGX_STRING);
    protected static final Pattern a_oIntegerPattern =  Pattern.compile(REGX_INTEGER);
    protected static final Pattern a_oRealPattern =  Pattern.compile(REGX_REAL);
    protected static final Pattern a_oBooleanPattern =  Pattern.compile(REGX_BOOLEAN);
    protected static final Pattern a_oNullPattern =  Pattern.compile(REGX_NULL);

    private Map<String, Serializable> a_lSettings = null;
    private UUID a_oIdentifier = null;
    private String a_sKeyPrefix = null;

    public Settings(UUID oIdentifier) {
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
        a_lSettings = new HashMap<>();
        a_sKeyPrefix = "";
    }

    public UUID GetIdentifier() { return a_oIdentifier; }

    public String GetString(String sSetting) {
        return String.valueOf(Get(sSetting));
    }

    public Serializable Get(String sSetting) {
        String sKey = a_sKeyPrefix + sSetting.toLowerCase();
        if(!a_lSettings.containsKey(sKey)) {
            return  null;
        }
        return a_lSettings.get(sKey);
    }

    public void Set(String sSetting, Serializable oValue) {
        a_lSettings.put(a_sKeyPrefix + sSetting.toLowerCase(), oValue);
    }

    public void SetSpecial(String sSetting, String sValue) {
        //TODO dirty and lazy way, improve to be proper use a automata mannager
        Matcher oStringMatcher = a_oStringPattern.matcher(sValue);
        Matcher oIntegerMatcher = a_oIntegerPattern.matcher(sValue);
        Matcher oRealMatcher = a_oRealPattern.matcher(sValue);
        Matcher oBooleanMatcher = a_oBooleanPattern.matcher(sValue);
        Matcher oNullMatcher = a_oNullPattern.matcher(sValue);

        System.out.printf("_WARNING: %ssimple and lazy implementation.\n",MLogManager.MethodName());

        Serializable oSetting = sValue;
        if (oStringMatcher.matches()) {
            oSetting = oStringMatcher.group(MATCH_GROUP);
        } else if (oIntegerMatcher.matches()) {
            oSetting = Integer.valueOf(oIntegerMatcher.group(MATCH_GROUP));
        } else if (oRealMatcher.matches()) {
            oSetting = Double.valueOf(oRealMatcher.group(MATCH_GROUP));
        } else if (oBooleanMatcher.matches()) {
            oSetting = Boolean.valueOf(oBooleanMatcher.group(MATCH_GROUP));
        } else if (oNullMatcher.matches()) {
            oSetting = null;
        }
        Set(sSetting, oSetting);
    }

    @Override
    public void SetKeyPrefix(String sKeyPrefix) {
        // TODO be careful when accessing using threads, make thread save.
        if (sKeyPrefix == null) { sKeyPrefix = ""; }
        a_sKeyPrefix = sKeyPrefix.toLowerCase();
    }

    public Iterable<Map.Entry<String,Serializable>> GetAll() {
        return a_lSettings.entrySet();
    }
}

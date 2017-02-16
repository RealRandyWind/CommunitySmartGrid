package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public interface ISettings {
    public UUID GetIdentifier();
    public String GetString(String sSetting);
    public Serializable Get(String sSetting);
    public void Set(String sSetting, Serializable oValue);
    public void SetSpecial(String sSetting, String sValue);
    public void SetKeyPrefix(String sKeyPrefix);
    public Iterable<Map.Entry<String,Serializable>> GetAll();
    // public ISetting[] ToSettings();
}

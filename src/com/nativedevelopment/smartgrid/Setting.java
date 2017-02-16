package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public class Setting implements ISetting {
	String a_sSetting = null;
	Serializable a_oSetting = null;

	public Setting(String sSetting, Serializable oSetting) {
		a_sSetting = sSetting;
		a_oSetting = oSetting;
	}

	@Override
	public String GetKey() {
		return a_sSetting;
	}

	@Override
	public Serializable GetValue() {
		return a_oSetting;
	}
}

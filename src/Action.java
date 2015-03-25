package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public class Action implements Serializable {
	public String deviceId;
	public EAction action;

	public Action(String deviceId, EAction action) {
		this.deviceId = deviceId;
		this.action = action;
	}

}

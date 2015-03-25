package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Action implements Serializable {
	public enum EAction {
		StopUsage,
		DecreaseUsage,
		IncreaseUsage,
		IncreaseProduction,
		DecreaseProduction
	}

	public UUID deviceId;
	public EAction action;

	public Action(UUID deviceId, EAction action) {
		this.deviceId = deviceId;
		this.action = action;
	}
}

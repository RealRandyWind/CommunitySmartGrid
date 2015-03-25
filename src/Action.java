package com.nativedevelopment.smartgrid;

public class Action {
	public enum EAction {
		StopUsage,
		DecreaseUsage,
		IncreaseUsage,
		IncreaseProduction,
		DecreaseProduction
	}

	public String deviceId;
	public EAction action;
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.net.InetAddress;
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
	public UUID clientId;
	public InetAddress clientIp;
	public EAction action;

	public Action(UUID deviceId, EAction action, UUID clientId, InetAddress clientIp) {
		this.deviceId = deviceId;
		this.action = action;
		this.clientId = clientId;
		this.clientIp = clientIp;
	}
}

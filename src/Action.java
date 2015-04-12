package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Action implements Serializable {
	public UUID deviceId;
	public UUID clientId;
	public InetAddress clientIp;
	public double newProduction; // negative for usage

	public Action(InetAddress clientIp, UUID clientId, UUID deviceId, double newproduction) {
		this.deviceId = deviceId;
		this.newProduction = newproduction;
		this.clientId = clientId;
		this.clientIp = clientIp;
	}
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

public class Data implements Serializable {
	public UUID deviceId;
    public UUID clientId;
	public InetAddress clientIp;
	/** Time of data */
	public long unixTimestamp;
	/** Usage in unit/second. Negative for production */
	public double usage;
	/** Potential production in next time interval */
	public double potentialProduction;
	public Location location;
}

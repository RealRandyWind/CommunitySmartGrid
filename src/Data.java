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
	/** Usage in unit/second. Negative for productoin */
	public double usage;
	/** Predicted usage in next time interval. Negative for production. */
	public double predictedUsage;
	/** Potential production in next time interval */
	public double potentialProduction;
	public Location location;
}

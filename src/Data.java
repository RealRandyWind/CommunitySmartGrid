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
	/** Usage in unit/second */
	public double usage;
	/** Production in unit/second */
	public double production;
	/** Predicted usage in next time interval */
	public double predictedUsage;
	/** Predicted production in next time interval */
	public double predictedProduction;
	/** Potential production in next time interval */
	public double potentialProduction;
	/** Length in seconds of next time interval */
	public int predictedTimeInterval;
}

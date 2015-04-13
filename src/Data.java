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
	/** Potential production */
	public double potentialProduction;
	/** Potential energy usage. The energy this device needs when turned on. Some devices might support running on
	 *  on less energy than the full potential usage. */
	public double potentialUsage;
	public Location location;

	public String toString() {
		return "[Data] DeviceId: " + deviceId +". Usage: " + usage + ". PotentialProduction: " + potentialProduction + ". PotentialUsage: " + potentialUsage;
	}
}

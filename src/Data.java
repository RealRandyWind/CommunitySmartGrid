package com.nativedevelopment.smartgrid;


public class Data {
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
	/** Length in seconds of next time interval */
	public int predictedTimeInterval;
}

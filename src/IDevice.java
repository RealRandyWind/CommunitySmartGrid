package com.nativedevelopment.smartgrid;


public interface IDevice {
	public String getIdentifier();
	public Data getData();
	public void performAction(Action a);
}

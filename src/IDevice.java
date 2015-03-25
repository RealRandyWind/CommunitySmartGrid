package com.nativedevelopment.smartgrid;

public interface IDevice {
	public String getIdentifier();
	public com.nativedevelopment.smartgrid.Data getData();
	public void performAction(com.nativedevelopment.smartgrid.Action a);
}

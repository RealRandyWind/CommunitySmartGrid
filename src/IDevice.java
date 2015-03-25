package com.nativedevelopment.smartgrid;

import java.util.UUID;

public interface IDevice {
	public UUID getIdentifier();
	public Data getData();
	public void performAction(Action a);
}

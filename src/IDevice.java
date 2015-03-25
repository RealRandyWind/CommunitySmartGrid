package com.nativedevelopment.smartgrid;

import java.util.UUID;

public interface IDevice extends IControllable {
	public UUID getIdentifier();
	public Data getData();
}

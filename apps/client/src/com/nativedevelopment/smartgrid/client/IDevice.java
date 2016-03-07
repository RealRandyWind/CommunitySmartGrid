package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.IControlable;

import java.util.UUID;

public interface IDevice extends IControlable {
	public UUID GetIdentifier();
	public String[] GetDataSpace();
}
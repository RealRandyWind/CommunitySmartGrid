package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface ISettingsMap extends Serializable {
	public UUID GetIdentifier();
	public Serializable[] GetKeys();
	public Serializable[] GetValues();
}

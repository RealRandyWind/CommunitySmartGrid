package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class ConfigureConnection implements IConfigureConnection {
	private UUID a_oTypeIdentifier = null;
	private ISetting[] a_lSettings = null;

	public ConfigureConnection(UUID oTypeIdentifier, ISetting[] lSettings) {
		a_oTypeIdentifier = oTypeIdentifier;
		a_lSettings = lSettings;
	}

	@Override
	public UUID GetTypeIdentifier() {
		return a_oTypeIdentifier;
	}

	@Override
	public ISetting[] GetSettings() {
		return a_lSettings;
	}
}

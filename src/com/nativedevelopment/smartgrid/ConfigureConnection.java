package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class ConfigureConnection implements IConfigureConnection {
	private UUID a_oIdentifier = null;
	private ISetting[] a_lSettings = null;

	public ConfigureConnection(UUID oIdentifier, ISetting[] lSettings) {
		a_oIdentifier = oIdentifier;
		a_lSettings = lSettings;
	}

	@Override
	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	@Override
	public ISetting[] GetSettings() {
		return a_lSettings;
	}
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class SettingsMap implements ISettingsMap {
	private UUID a_oIdentifier = null;
	private Serializable[] a_lKeys = null;
	private Serializable[] a_lValues = null;

	public SettingsMap(UUID oIdentifier, Serializable[] lKeys, Serializable[] lValues) {
		a_oIdentifier = oIdentifier;
		a_lKeys = lKeys;
		a_lValues = lValues;
	}

	@Override
	public UUID GetIdentifier() {
		return null;
	}

	@Override
	public Serializable[] GetKeys() {
		return new Serializable[0];
	}

	@Override
	public Serializable[] GetValues() {
		return new Serializable[0];
	}
}

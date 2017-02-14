package com.nativedevelopment.smartgrid;

import java.util.UUID;

public class Address implements IAddress {
	private UUID a_oIdentifier = null;
	private String a_sHost = null;
	private int a_iPort = -1;

	public Address(UUID oIdentifier, String sHost, int iPort) {
		a_oIdentifier = oIdentifier;
		a_sHost = sHost;
		a_iPort = iPort;
	}

	@Override
	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	@Override
	public String GetHost() {
		return a_sHost;
	}

	@Override
	public int GetPort() {
		return a_iPort;
	}
}

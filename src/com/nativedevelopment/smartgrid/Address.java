package com.nativedevelopment.smartgrid;

import java.util.UUID;

public class Address implements IAddress {
	private UUID a_oIdentifier = null;
	private UUID a_oContext = null;
	private String a_sHost = null;
	private int a_iPort = -1;

	public Address(UUID oIdentifier, UUID a_oContext, String sHost, int iPort) {
		a_oIdentifier = oIdentifier;
		a_oContext = a_oContext;
		a_sHost = sHost;
		a_iPort = iPort;
	}

	@Override
	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	@Override
	public UUID GetContext() {
		return a_oContext;
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

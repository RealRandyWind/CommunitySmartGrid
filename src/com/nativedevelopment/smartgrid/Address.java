package com.nativedevelopment.smartgrid;

import java.util.UUID;

public class Address implements IAddress {
	private UUID a_oConnectionIdentifier = null;
	private UUID a_oContext = null;
	private String a_sHost = null;
	private int a_iPort = -1;

	public Address(UUID oConnectionIdentifier, UUID oContext, String sHost, int iPort) {
		a_oConnectionIdentifier = oConnectionIdentifier;
		a_oContext = oContext;
		a_sHost = sHost;
		a_iPort = iPort;
	}

	@Override
	public UUID GetConnectionIdentifier() {
		return a_oConnectionIdentifier;
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

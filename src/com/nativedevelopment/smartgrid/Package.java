package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Package implements IPackage {
	private Serializable a_oContent = null;
	private UUID a_oRouteIdentifier = null;
	private UUID a_oCorrelationIdentifier = null;
	private int a_nFlag = 0;

	public Package (Serializable oContent, UUID oRouteIdentifier, UUID oCorrelationIdentifier, int nFlag) {
		a_oContent = oContent;
		a_oRouteIdentifier = oRouteIdentifier;
		a_oCorrelationIdentifier = oCorrelationIdentifier;
		a_nFlag = nFlag;
	}

	@Override
	public UUID GetRoutIdentifier() {
		return a_oRouteIdentifier;
	}

	@Override
	public UUID GetCorrelationIdentifier() {
		return a_oCorrelationIdentifier;
	}

	@Override
	public int GetFlag() {
		return a_nFlag;
	}

	@Override
	public Serializable GetContent() {
		return a_oContent;
	}
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Package implements IPackage {
	private Serializable a_oContent = null;
	private UUID a_oRouteIdentifier = null;
	private UUID a_oCorrelationIdentifier = null;
	private int a_nFlag = 0;
	private long a_tTimeStamp = 0;

	public Package (Serializable oContent, UUID oRouteIdentifier, UUID oCorrelationIdentifier, int nFlag, long tTimeStamp) {
		a_oContent = oContent;
		a_oRouteIdentifier = oRouteIdentifier;
		a_oCorrelationIdentifier = oCorrelationIdentifier;
		a_nFlag = nFlag;
		a_tTimeStamp = tTimeStamp;
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
	public long GetFlag() {
		return a_nFlag;
	}

	@Override
	public long GetTimeStamp() {
		return a_tTimeStamp;
	}

	@Override
	public Serializable GetContent() {
		return a_oContent;
	}
}

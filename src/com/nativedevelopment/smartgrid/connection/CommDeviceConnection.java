package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class CommDeviceConnection extends Connection{
	protected AbstractMap<UUID,Serializable> a_lActionMap = null;
	protected UUID a_iDevice = null;
	protected String a_lAttributes[] = null;

	public CommDeviceConnection(UUID oIdentifier, UUID iDevice, String[] lAttributes,
								AbstractMap<UUID, Serializable> lActionMap) {
		super(oIdentifier);
		a_lActionMap = lActionMap;
		a_iDevice = iDevice;
		a_lAttributes = lAttributes;
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n", MLogManager.MethodName());
	}
}

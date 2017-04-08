package com.nativedevelopment.smartgrid.connections;

import com.nativedevelopment.smartgrid.*;

import java.util.UUID;

public class CommDeviceConnection extends Connection{
	public CommDeviceConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n", MLogManager.MethodName());
	}
}

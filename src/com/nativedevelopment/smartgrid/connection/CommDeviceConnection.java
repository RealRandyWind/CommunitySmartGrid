package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.util.UUID;

public class CommDeviceConnection extends Connection{
	public CommDeviceConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void Run() {
		System.out.printf("_WARNING: [CommDeviceConnection.Run] not yet implemented\n");
	}
}

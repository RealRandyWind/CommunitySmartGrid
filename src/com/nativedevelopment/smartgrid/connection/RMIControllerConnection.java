package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.util.UUID;

public class RMIControllerConnection extends Connection {
	public RMIControllerConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void Run() {
		System.out.printf("_WARNING: [RMIControllerConnection.Run] not yet implemented\n");
	}
}

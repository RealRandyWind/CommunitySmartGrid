package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.util.UUID;

public class RMIControllerConnection extends Connection {
	public RMIControllerConnection(UUID oIdentifier, String sToHost, int iByPort, String lToService) {
		super(oIdentifier);
	}

	public void Run() {
		System.out.printf("_WARNING: [RMIControllerConnection.Run] not yet implemented\n");
	}
}

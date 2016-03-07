package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.rmi.Remote;
import java.util.UUID;

public class RMIControllerListenerConnection extends Connection{
	public RMIControllerListenerConnection(UUID oIdentifier, int iThroughPort, String lAsService, Remote oRemote) {
		super(oIdentifier);
	}

	public void Run() {
		System.out.printf("_WARNING: [RMIControllerListenerConnection.Run] not yet implemented\n");
	}
}

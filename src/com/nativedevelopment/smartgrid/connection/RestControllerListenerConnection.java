package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.rmi.Remote;
import java.util.UUID;

public class RestControllerListenerConnection extends Connection {
	public RestControllerListenerConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void Run() {
		System.out.printf("_WARNING: [RestControllerListenerConnection.Run] not yet implemented\n");
	}
}

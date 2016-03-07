package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class TCPConsumerConnection extends Connection {
	public TCPConsumerConnection(UUID oIdentifier, int iByPort, Queue<Serializable> lToQueue) {
		super(oIdentifier);
	}

	private Serializable Fx_Recive() {
		System.out.printf("_WARNING: [TCPConsumerConnection.Fx_Recive] not yet implemented\n");
		return null;
	}

	public void Run() {
		System.out.printf("_WARNING: [TCPConsumerConnection.Run] not yet implemented\n");
	}
}

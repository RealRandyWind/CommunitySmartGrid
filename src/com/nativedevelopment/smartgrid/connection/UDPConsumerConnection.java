package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class UDPConsumerConnection extends Connection {
	public UDPConsumerConnection(UUID oIdentifier, int iByPort, Queue<Serializable> lToQueue) {
		super(oIdentifier);
	}

	private Serializable Fx_Recive() {
		System.out.printf("_WARNING: [UDPConsumerConnection.Fx_Recive] not yet implemented\n");
		return null;
	}

	public void Run() {
		System.out.printf("_WARNING: [UDPConsumerConnection.Run] not yet implemented\n");
	}
}

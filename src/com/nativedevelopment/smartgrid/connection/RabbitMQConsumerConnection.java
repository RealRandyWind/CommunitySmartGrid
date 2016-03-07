package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQConsumerConnection extends Connection {
	public RabbitMQConsumerConnection(UUID oIdentifier,String sFromHost, String sFromQueue, Queue<Serializable> lToQueue) {
		super(oIdentifier);
	}

	private Serializable Fx_Recive() {
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.Fx_Recive] not yet implemented\n");
		return null;
	}

	public void Run() {
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.Run] not yet implemented\n");
	}
}

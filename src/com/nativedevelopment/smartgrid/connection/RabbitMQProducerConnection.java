package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQProducerConnection extends Connection {
	public RabbitMQProducerConnection(UUID oIdentifier,String sToHost, String sToQueue, Queue<Serializable> lFromQueue) {
		super(oIdentifier);
	}

	private void Fx_Send(Serializable oSerializable) {
		System.out.printf("_WARNING: [RabbitMQProducerConnection.Fx_Send] not yet implemented\n");
	}

	public void Run() {
		System.out.printf("_WARNING: [RabbitMQProducerConnection.Run] not yet implemented\n");
	}
}

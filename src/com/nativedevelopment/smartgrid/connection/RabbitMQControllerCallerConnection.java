package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.IPromise;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQControllerCallerConnection extends Connection {
	private IController a_oRemote = null;
	private IPromise a_oPromise = null;

	public RabbitMQControllerCallerConnection(UUID oIdentifier, Queue<Serializable> lToLogQueue, IPromise oPromise) {
		super(oIdentifier, lToLogQueue);
		// TODO replace oPromise by a Queue of Promises, create Fx_UpdatePromises function.
		a_oPromise = oPromise;
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: [RabbitMQControllerCallerConnection.Run] not yet implemented\n");
	}
}

package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IController;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQControllerListenerConnection extends Connection {
	private Queue<Serializable> a_lToLogQueue = null;
	private IController a_oRemote = null;
	private AbstractMap<Object, Remote> a_lRemotes = null;

	public RabbitMQControllerListenerConnection(UUID oIdentifier, IController oRemote, Queue<Serializable> lToLogQueue) {
		super(oIdentifier);
		a_lToLogQueue = lToLogQueue;
		a_oRemote = oRemote;
	}

	public void Run() {
		System.out.printf("_WARNING: [RabbitMQControllerListenerConnection.Run] not yet implemented\n");
	}
}

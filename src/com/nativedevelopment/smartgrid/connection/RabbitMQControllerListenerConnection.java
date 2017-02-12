package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.MLogManager;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQControllerListenerConnection extends Connection {
	private IController a_oRemote = null;
	private AbstractMap<Object, Remote> a_lRemotes = null;

	public RabbitMQControllerListenerConnection(UUID oIdentifier, IController oRemote, Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		a_oRemote = oRemote;
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n", MLogManager.MethodName());
	}
}

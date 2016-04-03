package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class SSHUserConnection extends Connection{
	public SSHUserConnection(UUID oIdentifier, Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
	}

	private void Fx_Command(Serializable oSerializable) {
		System.out.printf("_WARNING: [SSHUserConnection.Fx_Command] not yet implemented\n");
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: [SSHUserConnection.Run] not yet implemented\n");
	}
}

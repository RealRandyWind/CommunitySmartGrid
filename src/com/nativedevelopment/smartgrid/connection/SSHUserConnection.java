package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.MLogManager;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class SSHUserConnection extends Connection{
	public SSHUserConnection(UUID oIdentifier, Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
	}

	private void Fx_Command(Serializable oSerializable) {
		System.out.printf("_WARNING: %snot yet implemented\n", MLogManager.MethodName());
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
	}
}

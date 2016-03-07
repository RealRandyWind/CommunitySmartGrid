package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class UDPProducerConnection extends Connection{
	public UDPProducerConnection(UUID oIdentifier, String sToHost, int iByPort, Queue<Serializable> lFromQueue) {
		super(oIdentifier);
	}

	private void Fx_Send(Serializable oSerializable) {
		System.out.printf("_WARNING: [UDPProducerConnection.Fx_Send] not yet implemented\n");
	}

	public void Run() {
		System.out.printf("_WARNING: [UDPProducerConnection.Run] not yet implemented\n");
	}
}

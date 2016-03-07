package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class CassandraStorageConnection extends Connection {
	public CassandraStorageConnection(UUID oIdentifier, String sFromHost, String sToCluster, Queue<Serializable> lToQueue) {
		super(oIdentifier);
	}

	private void Fx_Store(Serializable oSerializable) {
		System.out.printf("_WARNING: [CassandraStorageConnection.Fx_Store] not yet implemented\n");
	}

	public void Run() {
		System.out.printf("_WARNING: [CassandraStorageConnection.Run] not yet implemented\n");
	}
}

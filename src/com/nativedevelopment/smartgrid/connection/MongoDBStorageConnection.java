package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class MongoDBStorageConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_KEYSPACE= "keyspace";
	public static final String SETTINGS_KEY_TABLE = "table";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private Queue<Serializable> a_lFromQueue = null;

	public MongoDBStorageConnection(UUID oIdentifier, Queue<Serializable> lFromQueue,
									Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: [MongoDBStorageConnection] no queue to produce from\n");
		}
		a_lFromQueue = lFromQueue;
	}

	private void Fx_Store(Serializable oSerializable) {
		System.out.printf("_WARNING: [MongoDBStorageConnection.Fx_Store] not yet implemented\n");
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: [MongoDBStorageConnection.Run] not yet implemented\n");
	}
}

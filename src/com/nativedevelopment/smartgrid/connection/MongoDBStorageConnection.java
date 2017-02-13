package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.MLogManager;

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

	public MongoDBStorageConnection(UUID oIdentifier, Queue<Serializable> lFromQueue,
									Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: %sno queue to produce from\n", MLogManager.MethodName());
		}
		a_lFromQueue = lFromQueue;
	}

	private void Fx_Store(Serializable oSerializable) {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
	}
}

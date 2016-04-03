package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;

import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by Gebruiker on 20/03/2016.
 */
public class LocalStorageConnection extends Connection {
	public static final String SETTINGS_KEY_FILE = "file";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private Queue<Serializable> a_lFromQueue = null;

	private RandomAccessFile a_oFile = null;

	public LocalStorageConnection(UUID oIdentifier, Queue<Serializable> lFromQueue, Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: [LocalStorageConnection] no queue to produce from\n");
		}
		a_lFromQueue = lFromQueue;
	}

	private void Fx_Store(Serializable oSerializable) {
		System.out.printf("_WARNING: [LocalStorageConnection.Fx_Store] not yet implemented\n");
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: [LocalStorageConnection.Run] not yet implemented\n");
	}
}

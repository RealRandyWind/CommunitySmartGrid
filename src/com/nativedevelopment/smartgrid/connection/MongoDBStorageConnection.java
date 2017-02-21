package com.nativedevelopment.smartgrid.connection;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IPackage;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class MongoDBStorageConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_DATABASE = "database";
	public static final String SETTINGS_KEY_COLLECTION = "collection";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private String a_sToHost = null;
	private int a_iThroughPort = 0;
	private String a_sKeySpace = null;
	private String a_sCollection = null;

	private Queue<Serializable> a_lFromQueue = null;

	public MongoDBStorageConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void SetFromQueue(Queue<Serializable> lFromQueue) {
		a_lFromQueue = lFromQueue;
	}

	private void Fx_Store() {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sToHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_iThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sKeySpace = oConfigurations.GetString(SETTINGS_KEY_DATABASE);
		a_sCollection = oConfigurations.GetString(SETTINGS_KEY_COLLECTION);
		a_nCheckTimeLowerBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND);
		a_nCheckTimeUpperBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND);
		a_nDeltaCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND);

		a_nCheckTime = a_nCheckTimeLowerBound;
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		MongoClient oMongoClient = new MongoClient(a_sToHost, a_iThroughPort);
		MongoDatabase oKeySpace = oMongoClient.getDatabase(a_sKeySpace);
		MongoCollection<Document> oCollection = oKeySpace.getCollection(a_sCollection);
		try {
			while (!IsClose()) {
				TimeOutRoutine(false);
				/*Document oDocument = new Document()
						.append("id", UUID.randomUUID().toString())
						.append("timestamp", 29384)
						.append("data", new Binary(rawBytes));*/
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s%s \"%s\"\n"
					,MLogManager.MethodName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

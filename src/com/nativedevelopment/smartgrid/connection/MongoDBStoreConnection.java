package com.nativedevelopment.smartgrid.connection;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nativedevelopment.smartgrid.*;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class MongoDBStoreConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_DATABASE = "database";
	public static final String SETTINGS_KEY_COLLECTION = "collection";
	public static final String SETTINGS_KEY_ISPACKAGEWRAPPED = "ispackagewrapped";
	public static final String SETTINGS_KEY_ISDOCUMENT = "isdocument";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String DOCUMENT_KEY_IDENTFIER = "_identifier";

	private String a_sToHost = null;
	private int a_iThroughPort = 0;
	private String a_sKeySpace = null;
	private String a_sCollection = null;
	private boolean a_bIsPackageWrapped = false;
	private boolean a_bIsDocument = false;

	protected TimeOut a_oTimeOut = null;
	protected Queue<Serializable> a_lFromQueue = null;

	public MongoDBStoreConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	public void SetFromQueue(Queue<Serializable> lFromQueue) {
		a_lFromQueue = lFromQueue;
	}

	private Serializable Fx_Store() throws Exception {
		if(a_lFromQueue == null) { return null; }
		Serializable ptrSerializable = a_lFromQueue.poll();
		if(ptrSerializable != null && (a_iRoute != null)) {
			IRoute oRoute = (IRoute) ptrSerializable;
			if(!oRoute.GetRouteId().equals(a_iRoute)) {
				a_lFromQueue.offer(ptrSerializable);
				ptrSerializable = null;
			} else { ptrSerializable = oRoute.GetContent(); }
		}
		a_oTimeOut.Routine(ptrSerializable == null);
		return ptrSerializable;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable sRouteId = oConfigurations.Get(SETTINGS_KEY_ROUTEID);
		a_iRoute = (sRouteId == null) ? null : UUID.fromString((String) sRouteId);
		a_sToHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_iThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sKeySpace = oConfigurations.GetString(SETTINGS_KEY_DATABASE);
		a_sCollection = oConfigurations.GetString(SETTINGS_KEY_COLLECTION);
		a_bIsPackageWrapped = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEWRAPPED);
		a_bIsDocument = (boolean)oConfigurations.Get(SETTINGS_KEY_ISDOCUMENT);
		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
		a_oTimeOut.SetUpperBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND));
		a_oTimeOut.SetDelta((int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND));
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		MongoClient oMongoClient = new MongoClient(a_sToHost, a_iThroughPort);
		MongoDatabase oKeySpace = oMongoClient.getDatabase(a_sKeySpace);
		MongoCollection<Document> oCollection = oKeySpace.getCollection(a_sCollection);
		try {
			while (!IsClose()) {
				Serializable ptrSerializable = Fx_Store();
				if(ptrSerializable == null) { continue; }
				Document oDocument;
				if(a_bIsDocument) {
					oDocument = (Document) ptrSerializable;
				} else if(a_bIsPackageWrapped) {
					IPackage oPackage = (IPackage) ptrSerializable;
					UUID iRoute =  oPackage.GetRoutIdentifier();
					UUID iCorrelation = oPackage.GetCorrelationIdentifier();
					oDocument = new Document().append("route", iRoute.toString())
						.append("timestamp", oPackage.GetTimeStamp())
						.append("correlation", iCorrelation.toString())
						.append("flag", oPackage.GetFlag())
						.append("content", new Binary(Serializer.Serialize(oPackage.GetContent(),0)));
				} else {
					oDocument = new Document().append("content", new Binary(Serializer.Serialize(ptrSerializable,0)));
				}
				oCollection.insertOne(oDocument);
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

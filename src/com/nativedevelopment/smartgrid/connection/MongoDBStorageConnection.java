package com.nativedevelopment.smartgrid.connection;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nativedevelopment.smartgrid.*;
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
	public static final String SETTINGS_KEY_ISPACKAGEWRAPPED = "ispackagewrapped";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String DOCUMENT_KEY_IDENTFIER = "_identifier";

	private String a_sToHost = null;
	private int a_iThroughPort = 0;
	private String a_sKeySpace = null;
	private String a_sCollection = null;
	private boolean a_bIsPackageWrapped = false;

	protected TimeOut a_oTimeOut = null;
	protected Queue<Serializable> a_lFromQueue = null;

	public MongoDBStorageConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	public void SetFromQueue(Queue<Serializable> lFromQueue) {
		a_lFromQueue = lFromQueue;
	}

	private Serializable Fx_Store() {
		if(a_lFromQueue == null) { return null; }
		Serializable ptrSerializable = a_lFromQueue.poll();
		return ptrSerializable;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sToHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_iThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sKeySpace = oConfigurations.GetString(SETTINGS_KEY_DATABASE);
		a_sCollection = oConfigurations.GetString(SETTINGS_KEY_COLLECTION);
		a_bIsPackageWrapped = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEWRAPPED);
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
				a_oTimeOut.Routine(false);
				Serializable ptrSerializable = Fx_Store();
				if(a_oTimeOut.Routine(ptrSerializable != null)) { continue; }
				Document oDocument = new Document();
				if(a_bIsPackageWrapped) {
					IPackage oPackage = (IPackage) ptrSerializable;
					UUID iRoute =  oPackage.GetRoutIdentifier();
					UUID iCorrelation = oPackage.GetCorrelationIdentifier();
					oDocument.append("route", iRoute.toString())
						.append("timestamp", oPackage.GetTimeStamp())
						.append("correlation", iCorrelation.toString())
						.append("flag", oPackage.GetFlag())
						.append("content", new Binary(Serializer.Serialize(oPackage.GetContent(),0)));
				} else {
					oDocument.append("content", new Binary(Serializer.Serialize(ptrSerializable,0)));
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

	public static Document DataToDocument(IData oData, int iTuple) {
		Document oDocument = new Document();
		UUID iData = oData.GetIdentifier();
		String[] lAttributes = oData.GetAttributes();
		Serializable[] lTuple = oData.GetTuple(iTuple);
		oDocument.append(DOCUMENT_KEY_IDENTFIER,iData);
		for (int iAttribute = 0; iAttribute < lAttributes.length; ++iAttribute) {
			oDocument.append(lAttributes[iAttribute],lTuple[iAttribute]);
		}
		return oDocument;
	}

	public static Document[] DataToDocuments(IData oData) {
		if(oData == null) { return null; }
		UUID iData = oData.GetIdentifier();
		Serializable[][] lTuples = oData.GetAllTuples();
		String[] lAttributes = oData.GetAttributes();
		Document[] lDocuments = new Document[lTuples.length];
		for(int iTuple = 0; iTuple < lTuples.length; ++iTuple) {
			lDocuments[iTuple].append(DOCUMENT_KEY_IDENTFIER,iData);
			for (int iAttribute = 0; iAttribute < lAttributes.length; ++iAttribute) {
				lDocuments[iTuple].append(lAttributes[iAttribute],lTuples[iTuple][iAttribute]);
			}
		}
		return lDocuments;
	}

	public static IData DocumentToData(Document oDocument) {
		int nTupleSize = oDocument.size()-1;
		int iAttribute = 0;
		int iTuple = 0;
		int nTuples = 1;
		UUID iData = (UUID)oDocument.get(DOCUMENT_KEY_IDENTFIER);
		String[] lAttributes = new String[nTupleSize];
		Serializable[][] lTuples = new Serializable[nTuples][nTupleSize];
		for (String sKey: oDocument.keySet()) {
			if(sKey.equals(DOCUMENT_KEY_IDENTFIER)) { continue; }
			lAttributes[iAttribute] = sKey;
			lTuples[iTuple][iAttribute] = (Serializable)oDocument.get(sKey);
			iAttribute++;
		}
		return new Data(iData,lTuples,lAttributes);
	}
}

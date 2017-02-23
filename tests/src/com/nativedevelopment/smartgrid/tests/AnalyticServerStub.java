package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.MongoDBStorageConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public final class AnalyticServerStub {
	MLogManager a_mLogManager = MLogManager.GetInstance();
	RabbitMQConsumerConnection a_oDataConsumer = null;
	RabbitMQProducerConnection a_oActionProducer = null;
	// TODO Storage, Monitor

	public AnalyticServerStub(String sRemote, int iPortRabbit, int iPortMongo) {
		a_oDataConsumer = new RabbitMQConsumerConnection(null);
		a_oActionProducer = new RabbitMQProducerConnection(null);
		ISettings oDataConsumerSettings = NewDataRealtimeConsumerSettings(sRemote, iPortRabbit, null);
		ISettings oActionProducerSettings = NewActionControlProducerSettings(sRemote, iPortMongo, null);
		a_oDataConsumer.Configure(oDataConsumerSettings);
		a_oActionProducer.Configure(oActionProducerSettings);
	}

	public void SetQueues(Queue<Serializable> lLogQueue,
						  Queue<Serializable> lDataQueue, Queue<Serializable> lActionQueue,
						  Queue<Serializable> lResultQueue, Queue<Serializable> lStateQueue) {
		a_oDataConsumer.SetToQueue(lDataQueue);
		a_oActionProducer.SetFromQueue(lActionQueue);
		a_oActionProducer.SetToLogQueue(lLogQueue);
		a_oDataConsumer.SetToLogQueue(lLogQueue);
	}

	public static final ISettings NewDataRealtimeConsumerSettings(String sRemote, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"data.realtime");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,false);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewActionControlProducerSettings(String sRemote, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"action.control");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"direct");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,true);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewResultStoreSettings(String sRemote, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,false);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_DATABASE,"8b9deeb8-ea9a-4a4e-93f3-a819b96c5620");
		oSettings.Set(MongoDBStorageConnection.SETTINGS_KEY_COLLECTION,"results");
		return oSettings;
	}

	public static final ISettings NewStateMonitorProducerSettings(String sRemote, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY,64);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_DELTACONNECTIONS,16);
		return oSettings;
	}

	public void Start() {
		a_mLogManager.Info("data.realtime.consumer \"%s\"",0,a_oDataConsumer.GetIdentifier().toString());
		a_mLogManager.Info("action.control.producer \"%s\"",0,a_oActionProducer.GetIdentifier().toString());

		a_oDataConsumer.Open();
		a_oActionProducer.Open();
	}

	public void Stop() {
		a_oActionProducer.Close();
		a_oDataConsumer.Close();
	}
}

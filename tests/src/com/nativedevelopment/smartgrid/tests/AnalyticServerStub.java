package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.connection.MongoDBStorageConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;

public final class AnalyticServerStub implements Runnable {
	private MLogManager a_mLogManager = MLogManager.GetInstance();
	private RabbitMQConsumerConnection a_oDataConsumer = null;
	private RabbitMQProducerConnection a_oActionProducer = null;
	// TODO Storage, Monitor

	private Thread a_oThread = null;
	private TimeOut a_oTimeOut = null;
	private UUID a_oIdentifier = null;
	volatile private boolean a_bIsStop = false;

	private Queue<Serializable> a_lLogQueue = null;
	private Queue<Serializable> a_lDataQueue = null;
	private Queue<Serializable> a_lActionQueue = null;
	private Queue<Serializable> a_lResultQueue = null;
	private Queue<Serializable> a_lStatusQueue = null;

	public AnalyticServerStub(UUID oIdentifier, String sRemote, int iPortRabbit, int iPortMongo, int iPortUDP) {
		a_oDataConsumer = new RabbitMQConsumerConnection(null);
		a_oActionProducer = new RabbitMQProducerConnection(null);
		ISettings oDataConsumerSettings = NewDataRealtimeConsumerSettings(sRemote, iPortRabbit, null);
		ISettings oActionProducerSettings = NewActionControlProducerSettings(sRemote, iPortRabbit, null);
		a_oDataConsumer.Configure(oDataConsumerSettings);
		a_oActionProducer.Configure(oActionProducerSettings);
		a_oThread = new Thread(this);
		a_oTimeOut = new TimeOut();
		a_oTimeOut.SetDelta(500);
		a_oTimeOut.SetLowerBound(5);
		a_oTimeOut.SetUpperBound(20000);
		a_oIdentifier = (oIdentifier == null ? UUID.randomUUID() : oIdentifier);
	}

	public void SetQueues(Queue<Serializable> lLogQueue,
						  Queue<Serializable> lDataQueue, Queue<Serializable> lActionQueue,
						  Queue<Serializable> lResultQueue, Queue<Serializable> lStatusQueue) {
		a_oDataConsumer.SetToQueue(lDataQueue);
		a_oActionProducer.SetFromQueue(lActionQueue);

		a_lLogQueue = lLogQueue;
		a_lDataQueue = lDataQueue;
		a_lActionQueue = lActionQueue;
		a_lResultQueue = lResultQueue;
		a_lStatusQueue = lStatusQueue;

		a_oActionProducer.SetToLogQueue(lLogQueue);
		a_oDataConsumer.SetToLogQueue(lLogQueue);
	}

	public void Start() {
		a_mLogManager.Info("data.realtime.consumer \"%s\"",0,a_oDataConsumer.GetIdentifier().toString());
		a_mLogManager.Info("action.control.producer \"%s\"",0,a_oActionProducer.GetIdentifier().toString());
		a_oDataConsumer.Open();
		a_oActionProducer.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oActionProducer.Close();
		a_oDataConsumer.Close();
		a_oThread.join();
	}

	private void Fx_DisplayData(IData oData) {
		int iTuple = 0;
		Serializable[] ptrTuple = oData.GetTuple(iTuple);
		while(ptrTuple == null) {
			a_mLogManager.Debug("data received \"%s\"",0, Arrays.toString(ptrTuple));
			++iTuple;
			ptrTuple =  oData.GetTuple(iTuple);
		}
	}

	@Override
	public void run() {
		a_mLogManager.Debug("%s running",0, a_oIdentifier.toString());
		try {
			while(!a_bIsStop) {
				Serializable ptrData = a_lDataQueue.poll();
				if(a_oTimeOut.Routine(ptrData == null)) { continue; }
				IData oData = (IData) ptrData;
				Fx_DisplayData(oData);
				IAction oAction = Generator.GenerateActionMachine(null);
				IPackage oPackage = new Package(oAction,oData.GetIdentifier(),null,0,System.currentTimeMillis());
				a_lActionQueue.add(oPackage);
			}
		} catch (Exception oException) {
			a_mLogManager.Warning("%s \"%s\"\n",0
					,a_oThread.getName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
			oException.printStackTrace();
		}
		a_mLogManager.Debug("%s stopped",0, a_oIdentifier.toString());
	}

	public static final ISettings NewDataRealtimeConsumerSettings(String sRemote, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"data.realtime");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,false);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_CHECKTIME,200000);
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
}

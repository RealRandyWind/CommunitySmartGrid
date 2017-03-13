package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;

import java.io.Serializable;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;

public final class AnalyticServerStub extends AServerStub {
	private RabbitMQConsumerConnection a_oDataConsumer = null;
	private RabbitMQProducerConnection a_oActionProducer = null;
	private UDPProducerConnection a_oHeartbeatProducer = null;
	// TODO Storage, Monitor

	private Queue<Serializable> a_lLogQueue = null;
	private Queue<Serializable> a_lDataQueue = null;
	private Queue<Serializable> a_lActionQueue = null;
	private Queue<Serializable> a_lResultQueue = null;
	private Queue<Serializable> a_lStatusQueue = null;

	public AnalyticServerStub(UUID oIdentifier, String sRemote, int iPortRabbit, int iPortMongo, int iPortUDP) {
		super(oIdentifier);
		a_oDataConsumer = new RabbitMQConsumerConnection(null);
		a_oActionProducer = new RabbitMQProducerConnection(null);
		a_oHeartbeatProducer = new UDPProducerConnection(null);
		ISettings oDataConsumerSettings = NewDataRealtimeConsumerSettings(sRemote, iPortRabbit, null);
		ISettings oActionProducerSettings = NewActionControlProducerSettings(sRemote, iPortRabbit, null);
		ISettings oHeartbeatProducerSettings = NewHeartbeatMonitorProducerSettings("localhost",iPortUDP,null);
		a_oDataConsumer.Configure(oDataConsumerSettings);
		a_oActionProducer.Configure(oActionProducerSettings);
		a_oHeartbeatProducer.Configure(oHeartbeatProducerSettings);
	}

	public void SetQueues(Queue<Serializable> lLogQueue, Queue<Serializable> lRemoteQueue,
						  Queue<Serializable> lDataQueue, Queue<Serializable> lActionQueue,
						  Queue<Serializable> lResultQueue, Queue<Serializable> lStatusQueue) {
		a_oDataConsumer.SetToQueue(lDataQueue);
		a_oActionProducer.SetFromQueue(lActionQueue);
		a_oHeartbeatProducer.SetFromQueue(lStatusQueue);
		a_oHeartbeatProducer.SetRemoteQueue(lRemoteQueue);


		a_lLogQueue = lLogQueue;
		a_lDataQueue = lDataQueue;
		a_lActionQueue = lActionQueue;
		a_lResultQueue = lResultQueue;
		a_lStatusQueue = lStatusQueue;

		a_oActionProducer.SetToLogQueue(lLogQueue);
		a_oDataConsumer.SetToLogQueue(lLogQueue);
		a_oHeartbeatProducer.SetToLogQueue(lLogQueue);
	}

	public void Start() {
		a_mLogManager.Info("data.realtime.consumer \"%s\"",0,a_oDataConsumer.GetIdentifier().toString());
		a_mLogManager.Info("action.control.producer \"%s\"",0,a_oActionProducer.GetIdentifier().toString());
		a_mLogManager.Info("state.monitor.producer \"%s\"",0, a_oHeartbeatProducer.GetIdentifier().toString());
		a_oDataConsumer.Open();
		a_oActionProducer.Open();
		a_oHeartbeatProducer.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oActionProducer.Close();
		a_oDataConsumer.Close();
		a_oHeartbeatProducer.Close();
		a_oThread.join();
	}

	@Override
	public void run() {
		a_mLogManager.Debug("%s running",0, GetIdentifier().toString());
		try {
			while(!a_bIsStop) {
				Serializable ptrData = a_lDataQueue.poll();
				if(a_oTimeOut.Routine(ptrData == null)) { continue; }
				IData oData = (IData) ptrData;
				DisplayData(oData, "received");
				IAction oAction = Generator.GenerateActionMachine(null);
				DisplayAction(oAction, "generated");
				IPackage oPackage = new Package(oAction,oData.GetIdentifier(),null,0,System.currentTimeMillis());
				a_lActionQueue.offer(oPackage);
				int nTuple = 1;
				UUID[] lActions = new UUID[1];
				lActions[0] = oAction.GetIdentifier();
				IData oResult = Generator.GenerateResult(oData.GetIdentifier(),nTuple,lActions);
				DisplayResult(oResult, "generated");
				a_lResultQueue.offer(oResult);
			}
		} catch (Exception oException) {
			a_mLogManager.Warning("%s \"%s\"\n",0
					,a_oThread.getName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
			oException.printStackTrace();
		}
		a_mLogManager.Debug("%s stopped",0, GetIdentifier().toString());
	}
}

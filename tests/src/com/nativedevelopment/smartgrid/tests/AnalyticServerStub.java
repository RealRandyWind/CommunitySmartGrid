package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.connection.*;

import java.io.Serializable;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.UUID;

public final class AnalyticServerStub extends AServerStub {
	/* Data Consumers */
	private RabbitMQConsumerConnection a_oDataConsumerRabbitMQ = null;
	private UDPConsumerConnection a_oDataConsumerUDP = null;
	private TCPConsumerConnection a_oDataConsumerTCP = null;
	private RabbitMQProducerConnection a_oActionProducer = null;
	private MongoDBStorageConnection a_oResultProducer = null;
	private RMIControllerListenerConnection a_oControllerListener = null;
	// TODO Storage, Monitor

	private Queue<Serializable> a_lLogQueue = null;
	private Queue<Serializable> a_lDataQueue = null;
	private Queue<Serializable> a_lActionQueue = null;
	private Queue<Serializable> a_lResultQueue = null;

	private IController a_oController = null;

	public AnalyticServerStub(UUID oIdentifier, String sRemote, String sLocal, int iPortRabbit, int iPortMongo, int iPortUDP, int iPortTCP, int iPortRMI) {
		super(oIdentifier);
		a_oDataConsumerRabbitMQ = new RabbitMQConsumerConnection(null);
		a_oDataConsumerUDP = new UDPConsumerConnection(null);
		a_oDataConsumerTCP = new TCPConsumerConnection(null);
		a_oActionProducer = new RabbitMQProducerConnection(null);
		a_oControllerListener = new RMIControllerListenerConnection(null);
		ISettings oDataConsumerSettingsRabbitMQ = NewDataRealtimeConsumerSettingsRabbitMQ(sRemote, iPortRabbit, null);
		ISettings oDataConsumerSettingsUDP = NewDataRealtimeConsumerSettingsUDP(sLocal, iPortUDP, null);
		ISettings oDataConsumerSettingsTCP = NewDataRealtimeConsumerSettingsTCP(sLocal, iPortTCP, null);
		ISettings oActionProducerSettings = NewActionControlProducerSettings(sRemote, iPortRabbit, null);
		ISettings oControllerListenerSettings = NewControllerListenerSettings(GetIdentifier().toString(), iPortRMI, null);
		a_oDataConsumerRabbitMQ.Configure(oDataConsumerSettingsRabbitMQ);
		a_oDataConsumerUDP.Configure(oDataConsumerSettingsUDP);
		a_oDataConsumerTCP.Configure(oDataConsumerSettingsTCP);
		a_oActionProducer.Configure(oActionProducerSettings);
		a_oControllerListener.Configure(oControllerListenerSettings);
	}

	public void SetQueues(Queue<Serializable> lLogQueue, Queue<Serializable> lDataQueue,
						  Queue<Serializable> lActionQueue, Queue<Serializable> lResultQueue) {
		a_lLogQueue = lLogQueue;
		a_lDataQueue = lDataQueue;
		a_lActionQueue = lActionQueue;
		a_lResultQueue = lResultQueue;

		a_oDataConsumerRabbitMQ.SetToQueue(lDataQueue);
		a_oDataConsumerUDP.SetToQueue(lDataQueue);
		a_oDataConsumerTCP.SetToQueue(lDataQueue);
		a_oActionProducer.SetFromQueue(lActionQueue);

		a_oActionProducer.SetToLogQueue(lLogQueue);
		a_oDataConsumerTCP.SetToLogQueue(lLogQueue);
		a_oDataConsumerUDP.SetToLogQueue(lLogQueue);
		a_oDataConsumerRabbitMQ.SetToLogQueue(lLogQueue);
	}

	public void SetControllers(IController oController) {
		a_oController = oController;

		a_oControllerListener.SetRemote(oController);
	}

	public void Start() {
		a_mLogManager.Info("data.realtime.consumer (RabbitMQ) \"%s\"",0, a_oDataConsumerRabbitMQ.GetIdentifier().toString());
		a_mLogManager.Info("data.realtime.consumer (UDP) \"%s\"",0, a_oDataConsumerUDP.GetIdentifier().toString());
		a_mLogManager.Info("data.realtime.consumer (TCP) \"%s\"",0, a_oDataConsumerTCP.GetIdentifier().toString());
		a_mLogManager.Info("action.control.producer (RabbitMQ) \"%s\"",0,a_oActionProducer.GetIdentifier().toString());
		a_mLogManager.Info("controller (RMI) \"%s\"",0,a_oControllerListener.GetIdentifier().toString());
		a_oDataConsumerRabbitMQ.Open();
		a_oDataConsumerUDP.Open();
		a_oDataConsumerTCP.Open();
		a_oActionProducer.Open();
		a_oControllerListener.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oControllerListener.Close();
		a_oActionProducer.Close();
		a_oDataConsumerRabbitMQ.Close();
		a_oDataConsumerTCP.Close();
		a_oDataConsumerUDP.Close();
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

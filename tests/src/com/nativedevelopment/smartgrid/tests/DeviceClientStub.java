package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class DeviceClientStub extends AServerStub {
	private RabbitMQProducerConnection a_oDataProducer = null;
	private RabbitMQConsumerConnection a_oActionConsumer = null;
	// TODO Storage, Monitor

	private Queue<Serializable> a_lLogQueue = null;
	private Queue<Serializable> a_lDataQueue = null;
	private Queue<Serializable> a_lActionQueue = null;

	public DeviceClientStub(UUID oIdentifier, String sRemote, int iPortRabbit, int iPortMongo, int iPortUDP) {
		super(oIdentifier);
		a_oDataProducer = new RabbitMQProducerConnection(null);
		a_oActionConsumer = new RabbitMQConsumerConnection(null);
		ISettings oDataProducerSettings = NewDataRealtimeProducerSettings(sRemote, iPortRabbit, null);
		ISettings oActionConsumerSettings = NewActionControlConsumerSettings(sRemote, iPortRabbit, GetIdentifier(), null);
		a_oDataProducer.Configure(oDataProducerSettings);
		a_oActionConsumer.Configure(oActionConsumerSettings);
	}

	public void SetQueues(Queue<Serializable> lLogQueue, Queue<Serializable> lDataQueue,
						  Queue<Serializable> lActionQueue) {
		a_oDataProducer.SetFromQueue(lDataQueue);
		a_oActionConsumer.SetToQueue(lActionQueue);

		a_lLogQueue = lLogQueue;
		a_lDataQueue = lDataQueue;
		a_lActionQueue = lActionQueue;

		a_oActionConsumer.SetToLogQueue(lLogQueue);
		a_oDataProducer.SetToLogQueue(lLogQueue);
	}

	public void Start() {
		a_mLogManager.Info("data.realtime.producer \"%s\"",0,a_oDataProducer.GetIdentifier().toString());
		a_mLogManager.Info("action.control.consumer \"%s\"",0,a_oActionConsumer.GetIdentifier().toString());
		a_oDataProducer.Open();
		a_oActionConsumer.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oActionConsumer.Close();
		a_oDataProducer.Close();
		a_oThread.join();
	}

	@Override
	public void run() {
		a_mLogManager.Debug("%s running",0, GetIdentifier().toString());
		try {
			while(!a_bIsStop) {
				int nTuples = 1;
				IData oData = Generator.GenerateDataMachine(GetIdentifier(),nTuples);
				DisplayData(oData, "generated");
				a_lDataQueue.offer(oData);
				Serializable ptrAction = a_lActionQueue.poll();
				if(ptrAction == null) { continue; }
				IAction oAction = (IAction) ptrAction;
				DisplayAction(oAction, "recived");
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

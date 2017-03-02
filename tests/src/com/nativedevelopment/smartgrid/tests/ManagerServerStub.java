package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Generator;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.connection.UDPConsumerConnection;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class ManagerServerStub extends AServerStub {
	private UDPConsumerConnection a_oStateConsumer = null;

	private Queue<Serializable> a_lStatusQueue = null;
	private Queue<Serializable> a_lLogQueue = null;

	public ManagerServerStub(UUID oIdentifier, int iPortUDP) {
		super(oIdentifier);
		a_oStateConsumer = new UDPConsumerConnection(null);
		ISettings oStateConsumerSettings = NewStateMonitorConsumerSettings("localhost",iPortUDP,null);
		a_oStateConsumer.Configure(oStateConsumerSettings);
	}

	public void SetQueues(Queue<Serializable> lLogQueue, Queue<Serializable> lStatusQueue) {
		a_oStateConsumer.SetToQueue(lStatusQueue);

		a_lLogQueue = lLogQueue;
		a_lStatusQueue = lStatusQueue;

		a_oStateConsumer.SetToLogQueue(lLogQueue);
	}

	public void Start() {
		a_mLogManager.Info("state.monitor.consumer \"%s\"",0,a_oStateConsumer.GetIdentifier().toString());
		a_oStateConsumer.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oStateConsumer.Close();
		a_oThread.join();
	}

	@Override
	public void run() {
		a_mLogManager.Debug("%s running",0, GetIdentifier().toString());
		try {
			while(!a_bIsStop) {
				Serializable ptrStatus = a_lStatusQueue.poll();
				if(a_oTimeOut.Routine(ptrStatus == null)) { continue; }
				// TODO
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

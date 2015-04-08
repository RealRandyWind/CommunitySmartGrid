package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.MConnectionManager;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.Serializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnalyticServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionManager = MConnectionManager.GetInstance();
	private Connection mConnection;
	private Channel mChannel;
	/** Maps stores the data history of the devices.
	 *  key: devideId
	 *  value: Data list */
	private Map<UUID, ArrayList<Data>> mDataHistory = new HashMap<UUID, ArrayList<Data>>();

	protected AnalyticServer() {

	}

	public void ShutDown() {
        mConnectionManager.ShutDown();
		mLogManager.ShutDown();

		try {
			if (mChannel != null)
				mChannel.close();
			if (mConnection != null)
				mConnection.close();
		} catch (IOException e) {
			mLogManager.Error(e.getMessage(), 0);
		}

		System.out.printf("_SUCCESS: [AnalyticServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			mConnection = factory.newConnection();
			mChannel = mConnection.createChannel();
		} catch (IOException e) {
			mLogManager.Error(e.toString(),0);
		}

		mLogManager.Success("[AnalyticServer.SetUp]", 0);

		// debug:
		mLogManager.Log("Running debug code.", 0);
        Data dummy = new Data();
        dummy.deviceId = UUID.randomUUID();
		dummy.clientId = UUID.fromString("3b287567-0813-4903-b7d6-e23bf5402c01"); //todo hardcoded
        dummy.potentialProduction = 100.0;
        this.OnDataReceived(dummy);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	public void Run() {

	}

    /**
     * Sends an action to the device.
     * @param action The action to send
     */
    private void sendAction(Action action) {
        // TODO: this code should be in the FakeMessageServer (and also the setup code)
        mLogManager.Log("Sending action to " + action.deviceId, 0);
        try {
            mChannel.queueDeclare("actions", false, false, false, null);
            mChannel.basicPublish("", "actions", null, Serializer.serialize(action));
        } catch (IOException e) {
            mLogManager.Error(e.toString(), 0);
        }
    }

	public static void main(String[] arguments) {
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}

	/**
	 * Uses a data package (and possibly the history) to determine if an action
	 * will be send to a device
	 * */
	private void AnalyzeData(Data data) {
		ArrayList<Data> datas = mDataHistory.get(data.deviceId);
		// iterate over all devices and find one with positive production

        for(ArrayList<Data> deviceData : mDataHistory.values()) {
            Data last = deviceData.get(deviceData.size()-1);
            if (last.potentialProduction > 0.0) {
                Action sinkAct = new Action(data.deviceId, Action.EAction.IncreaseUsage);
                Action sourceAct = new Action(last.deviceId, Action.EAction.IncreaseProduction);
                this.sendAction(sinkAct);
                this.sendAction(sourceAct);
                return;
            }
        }
	}

	/**
	 * Called when real-time data reaches the analytic server.
	 * @param data The data
	 */
	protected void OnDataReceived(Data data) {
		ArrayList<Data> datas;
		if (!mDataHistory.containsKey(data.deviceId)) {
			datas = new ArrayList<Data>();
			mDataHistory.put(data.deviceId, datas);
		} else {
			datas = mDataHistory.get(data.deviceId);
		}
		datas.add(data);
        this.AnalyzeData(data);
	}
}

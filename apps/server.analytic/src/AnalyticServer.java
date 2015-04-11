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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnalyticServer extends Main implements IAnalyticServer {
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

		try {
			IAnalyticServer stub = (IAnalyticServer) UnicastRemoteObject.exportObject(this, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("AnalyticServer", stub);

			mLogManager.Success("[AnalyticServer.SetUp] Server ready, bound in registry with name AnalyticServer", 0);
		} catch (Exception e) {
			mLogManager.Error("Server exception: " + e.toString(),0);
			e.printStackTrace();
		}

		mLogManager.Success("[AnalyticServer.SetUp]", 0);

		// debug:
		/*mLogManager.Log("Running debug code.", 0);
        Data dummy = new Data();
        dummy.deviceId = UUID.randomUUID();
		dummy.clientId = UUID.fromString(Config.TestClientUUID);
		try {
			dummy.clientIp = InetAddress.getByName(Config.TestClientHost);
		} catch (UnknownHostException e) {
			mLogManager.Error(e.getMessage(),0);
			this.ShutDown();
		}
		dummy.potentialProduction = 100.0;
        this.ReceiveData(dummy);*/
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
        mLogManager.Log("Sending action for " + action.clientId + " to message queue", 0);
		try {
            mChannel.queueDeclare("actions", false, false, false, null);
            mChannel.basicPublish("", "actions", null, Serializer.serialize(action));
        } catch (IOException e) {
            mLogManager.Error(e.getMessage(), 0);
        }
    }

	public static void main(String[] arguments) {
		Main oApplication = AnalyticServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
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
                Action sinkAct = new Action(data.deviceId, Action.EAction.IncreaseUsage, data.clientId, data.clientIp);
                Action sourceAct = new Action(last.deviceId, Action.EAction.IncreaseProduction, data.clientId, data.clientIp);
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
	public void ReceiveData(Data data) {
		mLogManager.Log("Received data from client " + data.clientId + " of device " + data.deviceId, 0);

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

package com.nativedevelopment.smartgrid.server.analytic;

import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.MConnectionManager;import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AnalyticServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	private Connection mConnection;
	private Channel mChannel;
	/** Maps stores the data history of the devices.
	 *  key: devideId
	 *  value: Data list */
	private Map<String, ArrayList<Data>> mDataHistory = new HashMap<String, ArrayList<Data>>();

	protected AnalyticServer() {

	}

	public void ShutDown() {
		mConnectionMannager.ShutDown();
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
		mConnectionMannager.SetUp();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			mConnection = factory.newConnection();
			mChannel = mConnection.createChannel();
		} catch (IOException e) {
			//e.printStackTrace();
			mLogManager.Error(e.toString(),0);
		}


		mLogManager.Success("[AnalyticServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new AnalyticServer();
		return a_oInstance;
	}

	public void Run() {
		try {
			mChannel.queueDeclare("actions", false, false, false, null);
			String message = "Hello World!";
			mChannel.basicPublish("", "actions", null, message.getBytes());
			mLogManager.Info(" [x] Sent '" + message + "'",0);
		} catch (IOException e) {
			mLogManager.Error(e.getMessage(), 0);
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
	}

	/**
	 * Called (by connection manager) when real-time data reaches the analytic server.
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
	}
}

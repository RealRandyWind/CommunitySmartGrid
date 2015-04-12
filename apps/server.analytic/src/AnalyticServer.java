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
	/** Maps stores the last Data of the devices.
	 *  key: devideId
	 *  value: Last data item */
	private Map<UUID, Data> lastDatas = new HashMap<UUID, Data>();

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
	 * Uses a data package to determine if an action
	 * will be send to a device
	 * */
	private void AnalyzeData(Data data) {
		double net_usage = 0.0;
		double net_potential_production = 0.0;
		// closest device with positive potential production
		Data closest_potential_production = null;
		double closest_potential_production_distance = Double.MAX_VALUE;
		// closest device with positive production (negative usage)
		Data closest_source = null;
		double closest_source_distance = Double.MAX_VALUE;

		// compute net usage, potential production and closest source and source with potential production
		for (Data d : lastDatas.values()) {
			net_usage += d.usage;
			net_potential_production += d.potentialProduction;
			if (d.potentialProduction > 0.0 && d.location.distanceTo(data.location) < closest_potential_production_distance) {
				closest_potential_production_distance = d.location.distanceTo(data.location);
				closest_potential_production = d;
			}
			if (d.usage < 0.0 && d.location.distanceTo(data.location) < closest_source_distance) {
				closest_source_distance = d.location.distanceTo(data.location);
				closest_source = d;
			}
		}

		if (closest_potential_production == null || closest_source == null) {
			mLogManager.Warning("Could not compute action. No data found.", 0);
			return;
		}

		mLogManager.Log("Net usage: "+net_usage+". Net potential production: "+net_potential_production,0);

		if (net_usage > 0.0 && net_potential_production > 0.0) {
			// use closest source to newest data point with potential production
			// add the minimum of potential production and what is needed
			double needed = net_usage;
			double newproduction = (closest_potential_production.usage*-1) + Math.min(needed, closest_potential_production.potentialProduction);
			Action act = new Action(closest_potential_production.clientIp, closest_potential_production.clientId, closest_potential_production.deviceId, newproduction);
			this.sendAction(act);
			mLogManager.Log("Send action to " + act.deviceId + " ("+act.clientIp+") to increase production to " + newproduction,0);
		} else if (net_usage < 0.0) {
			// surplus, tell nearest energy source to produce less (maximum of 0 with (current production-surplus))
			double newproduction = Math.max(closest_source.usage*-1 + net_usage, 0);
			Action act = new Action(closest_source.clientIp, closest_source.clientId, closest_source.deviceId, newproduction);
			this.sendAction(act);
			mLogManager.Log("Send action to " + act.deviceId + " ("+act.clientIp+") to decrease production to " + newproduction,0);
		}

	}

	/**
	 * Called when real-time data reaches the analytic server.
	 * @param data The data
	 */
	public void ReceiveData(Data data) {
		mLogManager.Log("Received data from client " + data.clientId + " of device " + data.deviceId, 0);

		lastDatas.put(data.deviceId, data);


        this.AnalyzeData(data);
	}
}

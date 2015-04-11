package com.nativedevelopment.smartgrid.server.datacollection;

import com.nativedevelopment.smartgrid.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DataCollectionServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();

	protected DataCollectionServer() {

	}

	public void ShutDown() {
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [DataCollectionServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();

		mLogManager.Success("[DataCollectionServer.SetUp]",0);
	}

	public void Run() {
		mLogManager.Info("Waiting for connection on port " + Config.Port_DataCollection, 0);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(Config.Port_DataCollection);
			byte[] buffer = new byte[1000];
			while (true) {
				DatagramPacket data = new DatagramPacket(buffer, buffer.length);
				socket.receive(data);
				mLogManager.Log("Received data from " + data.getAddress().toString(), 0);

				try {
					ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
					ObjectInputStream oos = new ObjectInputStream(baos);
					Data d = (Data)oos.readObject();
					mLogManager.Log("This data is from client " + d.clientId + " of device " + d.deviceId, 0);

					String host = Config.IP_Analytic;
					mLogManager.Debug("Locating the registry for the AnalyticServer, ip: " + host,0);
					Registry registry = LocateRegistry.getRegistry(host);
					mLogManager.Debug("Got registry for " + host, 0);
					IAnalyticServer analyticServer = (IAnalyticServer) registry.lookup("AnalyticServer");
					mLogManager.Debug("Looked up stub for AnalyticServer",0);
					analyticServer.ReceiveData(d);
					mLogManager.Log("Passed data to AnalyticServer",0);
				} catch (Exception e) {
					e.printStackTrace();
					mLogManager.Error("Exception: " + e.getMessage(),0);
				}
			}
		} catch (SocketException e) {
			mLogManager.Error("SocketException: " + e.getMessage(), 0);
		} catch (IOException e) {
			mLogManager.Error("IOException: " + e.getMessage(),0);
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new DataCollectionServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)	{
		Main oApplication = DataCollectionServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

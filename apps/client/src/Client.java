package com.nativedevelopment.smartgrid.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.DummyDevice;

public class Client extends Main implements IClient {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	private UUID uuid;

	protected InetAddress ip;

	private Map<UUID,IDevice> kvDevices = new HashMap<UUID,IDevice>();

	protected Client() {
		this.uuid = UUID.randomUUID();
	}

	public void ShutDown() {
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [Client.ShutDown] Thread from RMI still active in background.\n");
	}

	public void SetUp() {
		mLogManager.SetUp();

		try {
			IClient stub = (IClient) UnicastRemoteObject.exportObject(this, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Client" + this.getIdentifier(), stub); // todo allow multiple clients by appending "Client" with identifier

			mLogManager.Success("[Client.SetUp] Server ready, bound in registry with name Client"+this.getIdentifier(), 0);
		} catch (Exception e) {
			mLogManager.Error("Server exception: " + e.toString(),0);
			e.printStackTrace();
		}

		mLogManager.Success("[Client.SetUp]",0);
	}

	public void Run() {

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				this.ShutDown();
			}
			for (IDevice d : kvDevices.values()) {
				Data data = d.getData();
				data.clientId = this.getIdentifier();
				data.clientIp = this.ip;
				this.sendRealTimeData(data);
			}
		}
	}

	public void AddDevice(IDevice oDevice) {
		if(oDevice == null) {
			mLogManager.Warning("[Client.AddDevice] device to add is null",0);
			return;
		}

		kvDevices.put(oDevice.getIdentifier(),oDevice);
	}

	public IDevice GetDevice(UUID idDevice) {
		if(idDevice == null) {
			mLogManager.Warning("[Client.GetDevice] device id is null",0);
			return null;
		}

		return kvDevices.get(idDevice);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new Client();
		return a_oInstance;
	}

	public static void main(String[] arguments) {
		Client oApplication = (Client) Client.GetInstance();
		try {
			oApplication.ip = InetAddress.getByName(arguments[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		oApplication.mLogManager.Log("[Client.Run] running test",0);
		Location veendam = new Location(53.10627, 6.8751);
		DummyDevice test1 = new DummyDevice(veendam);
		test1.setCurrentUsage(Double.parseDouble(arguments[2]));
		test1.setPotentialProduction(Double.parseDouble(arguments[1]));
		oApplication.AddDevice(test1);

		int iEntryReturn = oApplication.Entry();
	}

    @Override
    public void passActionToDevice(Action action) throws RemoteException {
		mLogManager.Info("[Client.passActionToDevice] Received action:" + action.toString() + "\nSending to device.", 0);
		IDevice device = kvDevices.get(action.deviceId);
		if (device == null) {
			mLogManager.Error("Received action for device " + action.deviceId + ", but this device does not belong to this client",0);
		} else {
			device.performAction(action);
		}
    }

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	public void sendRealTimeData(Data data) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			oos.flush();
			byte[] databytes = baos.toByteArray();

			DatagramPacket msg = new DatagramPacket(databytes, databytes.length, InetAddress.getByName(Config.IP_DataCollection), Config.Port_DataCollection);
			socket.send(msg);
			mLogManager.Log("Send Real-Time data to CollectionServer. Bytes " + databytes.length,0);
		} catch (SocketException e) {
			mLogManager.Error("SocketException: " + e.getMessage(), 0);
		} catch (IOException e) {
			mLogManager.Error("IOException: " + e.getMessage(), 0);
		}
	}
}

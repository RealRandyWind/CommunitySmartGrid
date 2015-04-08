package com.nativedevelopment.smartgrid.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.IDevice;
import com.nativedevelopment.smartgrid.IClient;
import com.nativedevelopment.smartgrid.MConnectionManager;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.Config;

public class Client extends Main implements IClient {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	private UUID uuid;

	private Map<UUID,IDevice> kvDevices = new HashMap<UUID,IDevice>();

	protected Client() {
		this.uuid = UUID.randomUUID();
	}

	public void ShutDown() {
		mConnectionMannager.ShutDown();
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [Client.ShutDown] Thread from RMI still active in background.\n");
	}

	public void SetUp() {
		mLogManager.SetUp();
		mConnectionMannager.SetUp();

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
		mLogManager.Log("[Client.Run] running test",0);
		//mConnectionMannager.Run();
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

	public static void main(String[] arguments)	{
		Main oApplication = Client.GetInstance();
		int iEntryReturn = oApplication.Entry();
	}

    @Override
    public void passActionToDevice(Action action) throws RemoteException {
		mLogManager.Debug("[Client.passActionToDevice] called", 0);
    }

	@Override
	public UUID getIdentifier() {
		// TODO hardcoded
		return UUID.fromString("3b287567-0813-4903-b7d6-e23bf5402c01");
		//return this.uuid;
	}
}

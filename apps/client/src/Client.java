package com.nativedevelopment.smartgrid.client;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.nativedevelopment.smartgrid.IDevice;
import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.MConnectionManager;

public class Client extends Main implements IClient {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();

	private Map<UUID,IDevice> kvDevices = new HashMap<UUID,IDevice>();

	protected Client() {
	}

	public void ShutDown() {
		mConnectionMannager.ShutDown();
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [Client.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();
		mConnectionMannager.SetUp();

		mLogManager.Success("[Client.SetUp]",0);
	}

	public void Run	() {
		mLogManager.Log("[Client.Run] running",0);
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

	public static void main(String[] arguments)
	{
		Main oApplication = Client.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

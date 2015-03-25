package com.nativedevelopment.smartgrid.server.subscription;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.FakeSubscriptionServer;
import com.nativedevelopment.smartgrid.IClient;
import com.nativedevelopment.smartgrid.ISubscriptionServer;
import com.nativedevelopment.smartgrid.MConnectionManager;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Main;

import java.util.UUID;

public class SubscriptionServer extends Main implements ISubscriptionServer {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	
	protected SubscriptionServer() {

	}

	public void ShutDown() {
		mConnectionMannager.ShutDown();
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [SubscriptionServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();
		mConnectionMannager.SetUp();

		mLogManager.Success("[SubscriptionServer.SetUp]",0);
	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new SubscriptionServer();
		return a_oInstance;
	}

	public static void main(String[] arguments)
	{
		Main oApplication = SubscriptionServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}

    @Override
    public IClient getClient(UUID clientId) {
        return null;
    }
}

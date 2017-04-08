package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.server.analytic.AnalyticServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AnalyticServerTest implements ITestCase {
	AnalyticServer a_oApplication = null;
	MLogManager a_mLogManager = null;
	Thread a_oAnalyticServerThread = null;

	Deque<Serializable> a_lDataQueue = null;
	Deque<Serializable> a_lActionQueue = null;
	Queue<Serializable> a_lResultQueue = null;
	Queue<Serializable> a_lRemoteQueue = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (AnalyticServer)AnalyticServer.GetInstance();
		a_oAnalyticServerThread = new Thread(() -> a_oApplication.Entry());
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();
		a_lDataQueue = new ConcurrentLinkedDeque<>();
		a_lActionQueue = new ConcurrentLinkedDeque<>();
		a_lResultQueue = new ConcurrentLinkedDeque<>();
		a_lRemoteQueue = new ConcurrentLinkedDeque<>();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("begin",0);

		DeviceClientServerStub oDeviceClientStub = new DeviceClientServerStub(null,"localhost",5672,5673,5675);

		oDeviceClientStub.SetQueues(null,a_lDataQueue,a_lActionQueue);
		oDeviceClientStub.Start();
		a_oAnalyticServerThread.start();
		Thread.sleep(500);
		String sExchangeRMI = a_oApplication.GetIdentifier().toString();
		GUIAnalyticServerStub oGUIAnalyticServerStub = new GUIAnalyticServerStub(null,"localhost",sExchangeRMI,1099);
		oGUIAnalyticServerStub.Start();
		Thread.sleep(5000);
		oGUIAnalyticServerStub.Stop();
		oDeviceClientStub.Stop();
		a_oApplication.Exit();
		a_oAnalyticServerThread.join();
		a_mLogManager.Test("end",0);
	}
}
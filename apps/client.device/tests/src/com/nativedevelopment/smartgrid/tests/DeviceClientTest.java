package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.client.device.DeviceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeviceClientTest implements ITestCase {
	DeviceClient a_oApplication = null;
	MLogManager a_mLogManager = null;
	Thread a_oDeviceClientThread = null;

	Deque<Serializable> a_lDataQueue = null;
	Deque<Serializable> a_lActionQueue = null;
	Deque<Serializable> a_lResultQueue = null;
	IController a_oController = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (DeviceClient)DeviceClient.GetInstance();
		a_oDeviceClientThread = new Thread(() -> a_oApplication.Entry());
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();
		a_lDataQueue = new ConcurrentLinkedDeque<>();
		a_lActionQueue = new ConcurrentLinkedDeque<>();
		a_lResultQueue = new ConcurrentLinkedDeque<>();
		a_oController = new ControllerServerAnalyticStub();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("begin",0);
		AnalyticServerStub oAnalyticServerStub = new AnalyticServerStub(null,"localhost","localhost",5672,27017,5675,55539,1099);
		oAnalyticServerStub.SetQueues(null,a_lDataQueue,a_lActionQueue,a_lResultQueue);
		oAnalyticServerStub.SetControllers(a_oController);
		oAnalyticServerStub.Start();
		a_oDeviceClientThread.start();
		Thread.sleep(5000);
		oAnalyticServerStub.Stop();
		a_oApplication.Exit();
		a_oDeviceClientThread.join();
		a_mLogManager.Test("end",0);
	}
}
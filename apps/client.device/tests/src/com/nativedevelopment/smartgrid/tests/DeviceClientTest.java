package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.client.device.DeviceClient;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class DeviceClientTest implements ITestCase {
	DeviceClient a_oApplication = null;
	MLogManager a_mLogManager = null;
	Thread a_oDeviceClientThread = null;

	Queue<Serializable> a_lDataQueue = null;
	Queue<Serializable> a_lActionQueue = null;
	Queue<Serializable> a_lRemoteQueue = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (DeviceClient)DeviceClient.GetInstance();
		a_oDeviceClientThread = new Thread(() -> { a_oApplication.Entry(); });
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();
		a_lDataQueue = new ConcurrentLinkedQueue<>();
		a_lActionQueue = new ConcurrentLinkedQueue<>();
		a_lRemoteQueue = new ConcurrentLinkedQueue<>();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("begin",0);
		AnalyticServerStub oAnalyticServerStub = new AnalyticServerStub(null,"192.168.99.100",5672,5673,5675);
		oAnalyticServerStub.SetQueues(null,a_lRemoteQueue,a_lDataQueue,a_lActionQueue,null,null);
		oAnalyticServerStub.Start();
		a_oDeviceClientThread.start();
		Thread.sleep(5000);
		oAnalyticServerStub.Stop();
		a_oApplication.Exit();
		a_oDeviceClientThread.join();
		a_mLogManager.Test("end",0);
	}
}
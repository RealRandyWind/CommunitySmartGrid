package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IConnection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Settings;
import com.nativedevelopment.smartgrid.connection.UDPConsumerConnection;
import com.nativedevelopment.smartgrid.connection.UDPProducerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class UDPConnectionTest implements ITestCase {
	static final int SETTINGS_VALUE_BUFFERCAPACITY = 32;
	static final String SETTINGS_VALUE_HOST = "localhost";
	static final int SETTINGS_VALUE_PORT = 55535;

	public static final int TEST_MSG_COUNT = 20;

	MLogManager a_mLogManager = null;

	ISettings a_oProducerConfiguration = null;
	ISettings a_oConsumerConfiguration = null;

	Queue<Serializable> a_oFromQueue = null;
	Queue<Serializable> a_oToQueue = null;
	Queue<Serializable> a_oLogQueue = null;
	Queue<SocketAddress> a_lReceivers = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();

		a_oFromQueue = new ConcurrentLinkedQueue<>();
		a_oToQueue = new ConcurrentLinkedQueue<>();
		a_oLogQueue = new ConcurrentLinkedQueue<>();
		a_lReceivers = new ConcurrentLinkedQueue<>();

		a_oProducerConfiguration = new Settings(null);
		a_oConsumerConfiguration = new Settings(null);

		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_REMOTEPORT,SETTINGS_VALUE_PORT);
		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_LOCALADDRESS,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_LOCALPORT,55539);
		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);

		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_REMOTEADDRESS,null);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_REMOTEPORT,0);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_LOCALADDRESS,SETTINGS_VALUE_HOST);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_LOCALPORT,SETTINGS_VALUE_PORT);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);

		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oProducerConfiguration.Set(UDPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

		InetSocketAddress oReceiverAddress1 = new InetSocketAddress(SETTINGS_VALUE_HOST,55539);
		InetSocketAddress oReceiverAddress2 = new InetSocketAddress(SETTINGS_VALUE_HOST,55540);


		a_lReceivers.offer(oReceiverAddress1);
		a_lReceivers.offer(oReceiverAddress2);
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("[UDPConnectionTest.testRun] begin",0);
		IConnection oProducer = new UDPProducerConnection(null,a_oFromQueue,a_oLogQueue, a_lReceivers);
		IConnection oConsumer1 = new UDPConsumerConnection(null,a_oToQueue,a_oLogQueue);
		IConnection oConsumer2 = new UDPConsumerConnection(null,a_oToQueue,a_oLogQueue);

		a_mLogManager.Test("[UDPConnectionTest.testRun] Configure",0);
		oProducer.Configure(a_oProducerConfiguration);
		oConsumer1.Configure(a_oConsumerConfiguration);
		a_oConsumerConfiguration.Set(UDPConsumerConnection.SETTINGS_KEY_LOCALPORT,55540);
		oConsumer2.Configure(a_oConsumerConfiguration);

		assertTrue(a_oToQueue.isEmpty());

		a_mLogManager.Test("[UDPConnectionTest.testRun] Open",0);
		oConsumer1.Open();
		oConsumer2.Open();
		oProducer.Open();

		Thread.sleep(500);
		a_mLogManager.Test("[UDPConnectionTest.testRun] Run",0);
		for (int iIndex = 0; iIndex < TEST_MSG_COUNT; iIndex++) {
			Thread.sleep(100);
			a_oFromQueue.offer(String.format("Message %d.",iIndex));
		}
		Thread.sleep(1500);

		a_mLogManager.Test("[UDPConnectionTest.testRun] Close",0);
		oConsumer1.Close();
		oConsumer2.Close();
		oProducer.Close();

		Thread.sleep(2000);

		assertTrue(a_oFromQueue.isEmpty());
		assertFalse(a_oToQueue.isEmpty());
		assertEquals(TEST_MSG_COUNT*2,a_oToQueue.size());
		a_mLogManager.Test("[UDPConnectionTest.testRun] end",0);
	}
}
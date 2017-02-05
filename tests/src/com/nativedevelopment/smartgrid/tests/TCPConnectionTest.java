package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IConnection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Settings;
import com.nativedevelopment.smartgrid.connection.TCPConsumerConnection;
import com.nativedevelopment.smartgrid.connection.TCPProducerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class TCPConnectionTest implements ITestCase {
	static final int SETTINGS_VALUE_BUFFERCAPACITY = 64;
	static final String SETTINGS_VALUE_HOST = "localhost";

	public static final int TEST_MSG_COUNT = 20;

	MLogManager a_mLogManager = null;

	ISettings a_oProducerConfiguration = null;
	ISettings a_oConsumerConfiguration = null;

	Queue<Serializable> a_oFromQueue = null;
	Queue<Serializable> a_oToQueue = null;
	Queue<Serializable> a_oLogQueue = null;
	Queue<SocketAddress> a_lReceivers = null;
	Queue<SocketAddress> a_lSenders = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();

		a_oFromQueue = new ConcurrentLinkedQueue<>();
		a_oToQueue = new ConcurrentLinkedQueue<>();
		a_oLogQueue = new ConcurrentLinkedQueue<>();
		a_lReceivers = new ConcurrentLinkedQueue<>();
		a_lSenders = new ConcurrentLinkedQueue<>();

		a_oProducerConfiguration = new Settings(null);
		a_oConsumerConfiguration = new Settings(null);

		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALADDRESS,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALPORT,55539);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);

		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_DELTACONNECTIONS,16);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);

		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

		SocketAddress oReceiverAddress1 = new InetSocketAddress(SETTINGS_VALUE_HOST,55539);
		SocketAddress oReceiverAddress2 = new InetSocketAddress(SETTINGS_VALUE_HOST,55540);

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
		IConnection oProducer = new TCPProducerConnection(null,a_oFromQueue,a_oLogQueue,a_lReceivers);
		IConnection oConsumer1 = new TCPConsumerConnection(null,a_oToQueue,a_oLogQueue,a_lSenders);
		IConnection oConsumer2 = new TCPConsumerConnection(null,a_oToQueue,a_oLogQueue,a_lSenders);

		a_mLogManager.Test("[UDPConnectionTest.testRun] Configure",0);
		oProducer.Configure(a_oProducerConfiguration);
		oConsumer1.Configure(a_oConsumerConfiguration);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALPORT,55540);
		oConsumer2.Configure(a_oConsumerConfiguration);

		assertTrue(a_oToQueue.isEmpty());

		a_mLogManager.Test("[UDPConnectionTest.testRun] Open",0);
		oProducer.Open();
		oConsumer1.Open();
		oConsumer2.Open();

		Thread.sleep(500);
		a_mLogManager.Test("[UDPConnectionTest.testRun] Run",0);
		for (int iIndex = 0; iIndex < TEST_MSG_COUNT; iIndex++) {
			Thread.sleep(100);
			a_oFromQueue.offer(String.format("Message %d.",iIndex));
		}
		Thread.sleep(2000);

		a_mLogManager.Test("[UDPConnectionTest.testRun] Close",0);
		oConsumer1.Close();
		oConsumer2.Close();
		oProducer.Close();
		Thread.sleep(200);

		assertTrue(a_oFromQueue.isEmpty());
		assertFalse(a_oToQueue.isEmpty());
		assertEquals(TEST_MSG_COUNT*2,a_oToQueue.size());
		a_mLogManager.Test("[UDPConnectionTest.testRun] end",0);
	}
}
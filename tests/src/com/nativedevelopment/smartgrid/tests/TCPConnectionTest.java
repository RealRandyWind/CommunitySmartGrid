package com.nativedevelopment.smartgrid.tests;

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
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class TCPConnectionTest implements ITestCase {
	static final int SETTINGS_VALUE_BUFFERCAPACITY = 64;
	static final String SETTINGS_VALUE_HOST = "localhost";
	static final boolean SETTINGS_VALUE_ISPACKAGEUNWRAP = false;

	public static final int TEST_MSG_COUNT = 20;

	MLogManager a_mLogManager = null;

	ISettings a_oProducerConfiguration = null;
	ISettings a_oConsumerConfiguration = null;

	Deque<Serializable> a_oFromQueue = null;
	Deque<Serializable> a_oToQueue = null;
	Deque<Serializable> a_oLogQueue = null;
	Deque<Serializable> a_lReceivers = null;
	Deque<Serializable> a_lSenders = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();

		a_oFromQueue = new ConcurrentLinkedDeque<>();
		a_oToQueue = new ConcurrentLinkedDeque<>();
		a_oLogQueue = new ConcurrentLinkedDeque<>();
		a_lReceivers = new ConcurrentLinkedDeque<>();
		a_lSenders = new ConcurrentLinkedDeque<>();

		a_oProducerConfiguration = new Settings(null);
		a_oConsumerConfiguration = new Settings(null);

		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_ROUTEID,null);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALADDRESS,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALPORT,55539);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,SETTINGS_VALUE_ISPACKAGEUNWRAP);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_ROUTEID,null);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_DELTACONNECTIONS,16);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY,SETTINGS_VALUE_BUFFERCAPACITY);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oProducerConfiguration.Set(TCPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

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
		a_mLogManager.Test("begin",0);
		TCPProducerConnection oProducer = new TCPProducerConnection(null);
		TCPConsumerConnection oConsumer1 = new TCPConsumerConnection(null);
		TCPConsumerConnection oConsumer2 = new TCPConsumerConnection(null);

		oProducer.SetRemoteQueue(a_lReceivers);
		oProducer.SetToLogQueue(a_oLogQueue);
		oConsumer1.SetToLogQueue(a_oLogQueue);
		oConsumer2.SetToLogQueue(a_oLogQueue);
		oProducer.SetFromQueue(a_oFromQueue);
		oConsumer1.SetToQueue(a_oToQueue);
		oConsumer2.SetToQueue(a_oToQueue);

		a_mLogManager.Test("Configure",0);
		oProducer.Configure(a_oProducerConfiguration);
		oConsumer1.Configure(a_oConsumerConfiguration);
		a_oConsumerConfiguration.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALPORT,55540);
		oConsumer2.Configure(a_oConsumerConfiguration);

		assertTrue(a_oToQueue.isEmpty());

		a_mLogManager.Test("Open",0);
		oProducer.Open();
		oConsumer1.Open();
		oConsumer2.Open();

		Thread.sleep(500);
		a_mLogManager.Test("Run",0);
		for (int iIndex = 0; iIndex < TEST_MSG_COUNT; iIndex++) {
			Thread.sleep(100);
			a_oFromQueue.offer(String.format("Message %d.",iIndex));
		}
		Thread.sleep(2000);

		a_mLogManager.Test("Close",0);
		oConsumer1.Close();
		oConsumer2.Close();
		oProducer.Close();
		Thread.sleep(200);

		assertTrue(a_oFromQueue.isEmpty());
		assertFalse(a_oToQueue.isEmpty());
		assertEquals(TEST_MSG_COUNT * 2,a_oToQueue.size());
		a_mLogManager.Test("end",0);
	}
}
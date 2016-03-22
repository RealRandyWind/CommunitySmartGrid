package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IConnection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Settings;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class RabbitMQConnectionTest implements ITestCase {
	static final String SETTINGS_VALUE_HOST = "192.168.178.12";
	static final int SETTINGS_VALUE_PORT = 5672;
	static final String SETTINGS_VALUE_USER = "guest";
	static final String SETTINGS_VALUE_PASSWORD = "guest";

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

		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_HOST,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_PORT,SETTINGS_VALUE_PORT);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"test");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERNAME,SETTINGS_VALUE_USER);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERPASSWORD,SETTINGS_VALUE_PASSWORD);

		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);

		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_HOST,SETTINGS_VALUE_HOST);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_PORT,SETTINGS_VALUE_PORT);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"test");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		a_oConsumerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERNAME,SETTINGS_VALUE_USER);
		a_oConsumerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERPASSWORD,SETTINGS_VALUE_PASSWORD);
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("[RabbitMQConnectionTest.testRun] begin",0);
		IConnection oProducer = new RabbitMQProducerConnection(null,a_oFromQueue,a_oLogQueue);
		IConnection oConsumer = new RabbitMQConsumerConnection(null,a_oToQueue,a_oLogQueue);

		oProducer.Configure(a_oProducerConfiguration);
		oConsumer.Configure(a_oConsumerConfiguration);

		assertTrue(a_oToQueue.isEmpty());

		oConsumer.Open();
		oProducer.Open();

		Thread.sleep(500);
		for (int iIndex = 0; iIndex < TEST_MSG_COUNT; iIndex++) {
			Thread.sleep(100);
			a_oFromQueue.offer(String.format("Message %d.",iIndex));
		}
		Thread.sleep(1500);

		oConsumer.Close();
		oProducer.Close();

		Thread.sleep(2000);

		assertTrue(a_oFromQueue.isEmpty());
		assertFalse(a_oToQueue.isEmpty());
		a_mLogManager.Test("[RabbitMQConnectionTest.testRun] end",0);
	}
}
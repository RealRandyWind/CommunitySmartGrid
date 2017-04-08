package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Package;
import com.nativedevelopment.smartgrid.connection.RabbitMQConsumerConnection;
import com.nativedevelopment.smartgrid.connection.RabbitMQProducerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.Assert.*;

public class RabbitMQConnectionTest implements ITestCase {
	static final String SETTINGS_VALUE_HOST = "localhost";
	static final int SETTINGS_VALUE_PORT = 5672;
	static final String SETTINGS_VALUE_USER = "guest";
	static final String SETTINGS_VALUE_PASSWORD = "guest";
	static final boolean SETTINGS_VALUE_ISPACKAGEWRAPPED = false;
	static final boolean SETTINGS_VALUE_ISPACKAGEUNWRAP = false;

	public static final int TEST_MSG_COUNT = 20;

	MLogManager a_mLogManager = null;

	ISettings a_oProducerConfiguration = null;
	ISettings a_oConsumerConfiguration = null;

	Deque<Serializable> a_oFromQueue = null;
	Deque<Serializable> a_oToQueue = null;
	Deque<Serializable> a_oToQueue2 = null;
	Deque<Serializable> a_oLogQueue = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();

		a_oFromQueue = new ConcurrentLinkedDeque<>();
		a_oToQueue = new ConcurrentLinkedDeque<>();
		a_oToQueue2 = new ConcurrentLinkedDeque<>();
		a_oLogQueue = new ConcurrentLinkedDeque<>();

		a_oProducerConfiguration = new Settings(null);
		a_oConsumerConfiguration = new Settings(null);

		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTEID,null);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,SETTINGS_VALUE_HOST);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEPORT,SETTINGS_VALUE_PORT);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"test");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_CHECKTIME, 20000);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERNAME,SETTINGS_VALUE_USER);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERPASSWORD,SETTINGS_VALUE_PASSWORD);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,SETTINGS_VALUE_ISPACKAGEUNWRAP);

		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTEID,null);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_REMOTEADRESS,SETTINGS_VALUE_HOST);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_REMOTEPORT,SETTINGS_VALUE_PORT);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"test");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERNAME,SETTINGS_VALUE_USER);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERPASSWORD,SETTINGS_VALUE_PASSWORD);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,SETTINGS_VALUE_ISPACKAGEWRAPPED);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRunSubscribe() throws Exception {
		a_mLogManager.Test("begin",0);
		RabbitMQProducerConnection oProducer = new RabbitMQProducerConnection(null);
		RabbitMQConsumerConnection oConsumer = new RabbitMQConsumerConnection(null);

		oProducer.SetFromQueue(a_oFromQueue);
		oConsumer.SetToQueue(a_oToQueue);

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
		assertEquals(TEST_MSG_COUNT,a_oToQueue.size());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testRunRoute() throws Exception {
		a_mLogManager.Test("begin",0);
		RabbitMQProducerConnection oProducer = new RabbitMQProducerConnection(null);
		RabbitMQConsumerConnection oConsumer1 = new RabbitMQConsumerConnection(null);
		RabbitMQConsumerConnection oConsumer2 = new RabbitMQConsumerConnection(null);

		oProducer.SetFromQueue(a_oFromQueue);
		oConsumer1.SetToQueue(a_oToQueue);
		oConsumer2.SetToQueue(a_oToQueue2);

		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"test2");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"direct");
		a_oProducerConfiguration.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,true);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"test2");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"direct");
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,true);

		oProducer.Configure(a_oProducerConfiguration);

		UUID iConsumer1 = oConsumer1.GetIdentifier();
		UUID iConsumer2 = oConsumer2.GetIdentifier();

		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,iConsumer1.toString());
		oConsumer1.Configure(a_oConsumerConfiguration);
		a_oConsumerConfiguration.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,iConsumer2.toString());
		oConsumer2.Configure(a_oConsumerConfiguration);

		assertTrue(a_oToQueue.isEmpty());
		assertTrue(a_oToQueue2.isEmpty());

		oConsumer1.Open();
		oConsumer2.Open();
		oProducer.Open();

		Thread.sleep(500);
		for (int iIndex = 0; iIndex < TEST_MSG_COUNT; iIndex++) {
			Thread.sleep(100);
			a_oFromQueue.offer(new Package(String.format("Message to C1 %d.",iIndex)
					,iConsumer1,null,0,System.currentTimeMillis()));
			a_oFromQueue.offer(new Package(String.format("Message to C2 %d.",iIndex)
					,iConsumer2,null,0,System.currentTimeMillis()));
		}
		Thread.sleep(1500);

		oConsumer1.Close();
		oConsumer2.Close();
		oProducer.Close();

		Thread.sleep(2000);

		assertTrue(a_oFromQueue.isEmpty());
		assertFalse(a_oToQueue.isEmpty());
		assertFalse(a_oToQueue2.isEmpty());
		assertEquals(TEST_MSG_COUNT, a_oToQueue.size());
		assertEquals(TEST_MSG_COUNT, a_oToQueue2.size());
		a_mLogManager.Test("end",0);
	}
}
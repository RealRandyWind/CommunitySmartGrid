package com.nativedevelopment.smartgrid.server.message;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Serializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

public class MessageServer extends Main {
	private MLogManager mLogManager = MLogManager.GetInstance();
	private MConnectionManager mConnectionMannager = MConnectionManager.GetInstance();
	private Channel channel;
	public final static String ACTION_QUEUE_NAME = "actions";
	
	protected MessageServer() {

	}

	public void ShutDown() {
		mConnectionMannager.ShutDown();
		mLogManager.ShutDown();

		System.out.printf("_SUCCESS: [MessageServer.ShutDown]\n");
	}

	public void SetUp() {
		mLogManager.SetUp();
		mConnectionMannager.SetUp();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = null;
		mLogManager.Info("[MessageServer.SetUp] Connecting to localhost...", 0);
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(ACTION_QUEUE_NAME, false, false, false, null);

			mLogManager.Success("[MessageServer.SetUp] Connected", 0);
			mLogManager.Success("[MessageServer.SetUp]", 0);
		} catch (IOException e) {
			mLogManager.Error(e.getMessage(), 0);
		}

	}

	public static Main GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MessageServer();
		return a_oInstance;
	}

	public void Run() {


		QueueingConsumer consumer = new QueueingConsumer(channel);
		try {
			channel.basicConsume(ACTION_QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			mLogManager.Error(e.getMessage(), 0);
		}
		mLogManager.Info("[MessageServer.Run] Waiting for messages...", 0);
		while (true) {
			QueueingConsumer.Delivery delivery = null;
			try {
				delivery = consumer.nextDelivery();
                Action a = (Action) Serializer.deserialize(delivery.getBody());
				mLogManager.Info("Received action for '" + a.deviceId + "'",0);
                // hier is een action, doorsturen naar client application
                // TODO zodra we dat kunnen


			} catch (InterruptedException e) {
				mLogManager.Error(e.getMessage(),0);
			}
		}
	}

	public static void main(String[] arguments)	{
		Main oApplication = MessageServer.GetInstance();
		int iEntryReturn = oApplication.Entry();
		System.exit(iEntryReturn);
	}
}

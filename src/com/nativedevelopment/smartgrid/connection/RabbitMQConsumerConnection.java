package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;
import com.rabbitmq.client.*;

import java.io.*;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQConsumerConnection extends Connection implements Consumer {
	public static final String SETTINGS_KEY_REMOTEADRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_EXCHANGE= "exchange";
	public static final String SETTINGS_KEY_EXCHANGETYPE = "exchange.type";
	public static final String SETTINGS_KEY_ROUTINGKEY = "routing.key";
	public static final String SETTINGS_KEY_ISHANDSHAKE = "ishandshake";
	public static final String SETTINGS_KEY_USERNAME = "user.name";
	public static final String SETTINGS_KEY_USERPASSWORD = "user.password";

	private Queue<Serializable> a_lToQueue = null;
	private Queue<Serializable> a_lToLogQueue = null;

	private String a_sFromHost = null;
	private int a_nThroughPort = 0;
	private String a_sFromExchange = null;
	private String a_sTypeExchange = null;
	private String a_sFromQueue = null;
	private String a_sRoutingKey = null;
	private boolean a_bIsHandshake = false;
	private String a_sUserName = null;
	private String a_sUserPassword = null;

	private ConnectionFactory a_oRabbitMQConnectionFactory = null;
	private Channel a_oRabbitMQChannel = null;
	private com.rabbitmq.client.Connection a_oRabbitMQConnection = null;

	public RabbitMQConsumerConnection(UUID oIdentifier, Queue<Serializable> lToQueue, Queue<Serializable> lToLogQueue) {
		super(oIdentifier);
		if(lToQueue == null) {
			System.out.printf("_WARNING: [RabbitMQConsumerConnection] no queue to consume to\n");
		}
		a_lToQueue = lToQueue;
		a_lToLogQueue = lToLogQueue;
		a_bIsHandshake = true;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		Serializable oSerializable = Serializer.Deserialize(rawBytes,0);
		a_lToQueue.offer(oSerializable);
		System.out.printf("_DEBUG: [RabbitMQConsumerConnection.Fx_Consume] %d is producing %s\n",this.hashCode(),String.valueOf(oSerializable));
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sFromHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADRESS);
		a_nThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sFromExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sTypeExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGETYPE);
		a_sRoutingKey = oConfigurations.GetString(SETTINGS_KEY_ROUTINGKEY);
		a_bIsHandshake = (boolean)oConfigurations.Get(SETTINGS_KEY_ISHANDSHAKE);
		a_sUserName = oConfigurations.GetString(SETTINGS_KEY_USERNAME);
		a_sUserPassword = oConfigurations.GetString(SETTINGS_KEY_USERPASSWORD);
	}

	@Override
	public void Run() {
		try {
			a_oRabbitMQConnectionFactory = new ConnectionFactory();
			a_oRabbitMQConnectionFactory.setHost(a_sFromHost);
			a_oRabbitMQConnectionFactory.setPort(a_nThroughPort);
			a_oRabbitMQConnectionFactory.setUsername(a_sUserName);
			a_oRabbitMQConnectionFactory.setPassword(a_sUserPassword);
			a_oRabbitMQConnection = a_oRabbitMQConnectionFactory.newConnection();
			a_oRabbitMQChannel = a_oRabbitMQConnection.createChannel();
			a_oRabbitMQChannel.exchangeDeclare(a_sFromExchange, a_sTypeExchange);
			a_sFromQueue = a_oRabbitMQChannel.queueDeclare().getQueue();
			a_oRabbitMQChannel.queueBind(a_sFromQueue, a_sFromExchange, a_sRoutingKey);
			a_oRabbitMQChannel.basicConsume(a_sFromQueue, a_bIsHandshake, this);
		} catch (Exception oException) {
			System.out.printf("_WARNING: [RabbitMQConsumerConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}

	@Override
	public void handleConsumeOk(String s) {
		//TODO write message to log queue
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleConsumeOk] not yet implemented\n");
	}

	@Override
	public void handleCancelOk(String s) {
		//TODO write message to log queue
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleCancelOk] not yet implemented\n");
	}

	@Override
	public void handleCancel(String s) throws IOException {
		//TODO write message to log queue
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleCancel] not yet implemented\n");
	}

	@Override
	public void handleShutdownSignal(String s, ShutdownSignalException e) {
		//TODO write message to log queue
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleShutdownSignal] not yet implemented\n");
	}

	@Override
	public void handleRecoverOk(String s) {
		//TODO write message to log queue
		System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleRecoverOk] not yet implemented\n");
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		try {
			if(body == null) { return; }
			Fx_Consume(body);
		} catch (Exception oException) {
			//TODO write errors to log queue
			System.out.printf("_WARNING: [RabbitMQConsumerConnection.handleDelivery] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

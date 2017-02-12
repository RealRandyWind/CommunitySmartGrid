package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.Queue;
import java.util.UUID;

public class RabbitMQProducerConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_EXCHANGE= "exchange";
	public static final String SETTINGS_KEY_EXCHANGETYPE = "exchange.type";
	public static final String SETTINGS_KEY_ROUTINGKEY = "routing.key";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String SETTINGS_KEY_USERNAME = "user.name";
	public static final String SETTINGS_KEY_USERPASSWORD = "user.password";

	private Queue<Serializable> a_lFromQueue = null;

	private String a_sToHost = null;
	private int a_nThroughPort = 0;
	private String a_sToExchange = null;
	private String a_sTypeExchange = null;
	private String a_sRoutingKey = null;
	private String a_sUserName = null;
	private String a_sUserPassword = null;

	private int a_nCheckTime = 0;
	private int a_nCheckTimeLowerBound = 0;
	private int a_nCheckTimeUpperBound = 0;
	private int a_nDeltaCheckTime = 0;

	private ConnectionFactory a_oRabbitMQConnectionFactory = null;
	private Channel a_oRabbitMQChannel = null;
	private com.rabbitmq.client.Connection a_oRabbitMQConnection = null;

	public RabbitMQProducerConnection(UUID oIdentifier, Queue<Serializable> lFromQueue,
									  Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: [RabbitMQProducerConnection] no queue to produce from\n");
		}
		a_lFromQueue = lFromQueue;
	}

	private byte[] Fx_Produce() throws Exception {
		Serializable oSerializable = a_lFromQueue.poll();
		if(TimeOutRoutine(oSerializable==null))
		{
			return null;
		}
		System.out.printf("_DEBUG: [RabbitMQProducerConnection.Fx_Produce] %d is producing \"%s\"\n"
				,this.hashCode(),String.valueOf(oSerializable));
		return Serializer.Serialize(oSerializable, 0);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sToHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADRESS);
		a_nThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sToExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sTypeExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGETYPE);
		a_sRoutingKey = oConfigurations.GetString(SETTINGS_KEY_ROUTINGKEY);
		a_nCheckTimeLowerBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND);
		a_nCheckTimeUpperBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND);
		a_nDeltaCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND);
		a_sUserName = oConfigurations.GetString(SETTINGS_KEY_USERNAME);
		a_sUserPassword = oConfigurations.GetString(SETTINGS_KEY_USERPASSWORD);

		a_nCheckTime = a_nCheckTimeLowerBound;
	}

	@Override
	public void Run() {
		try {
			a_oRabbitMQConnectionFactory = new ConnectionFactory();
			a_oRabbitMQConnectionFactory.setHost(a_sToHost);
			a_oRabbitMQConnectionFactory.setPort(a_nThroughPort);
			// TODO fix support for authentication
			//a_oRabbitMQConnectionFactory.setUsername(a_sUserName);
			//a_oRabbitMQConnectionFactory.setPassword(a_sUserPassword);
			a_oRabbitMQConnection = a_oRabbitMQConnectionFactory.newConnection();
			a_oRabbitMQChannel = a_oRabbitMQConnection.createChannel();
			a_oRabbitMQChannel.exchangeDeclare(a_sToExchange, a_sTypeExchange);

			while(!IsClose()){
				byte[] rawBytes = Fx_Produce();
				if(rawBytes == null) { continue; }
				a_oRabbitMQChannel.basicPublish(a_sToExchange, a_sRoutingKey, null, rawBytes);
			}
		} catch (Exception oException) {
			//TODO write errors to log queue
			System.out.printf("_WARNING: [RabbitMQProducerConnection.Run] %s \"%s\"\n"
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

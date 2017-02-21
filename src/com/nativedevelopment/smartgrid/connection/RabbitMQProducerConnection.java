package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
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

	public static final String SETTINGS_KEY_ISAUTHENTICATE = "isauthenticate";
	public static final String SETTINGS_KEY_USERNAME = "user.name";
	public static final String SETTINGS_KEY_USERPASSWORD = "user.password";

	private String a_sToHost = null;
	private int a_iThroughPort = 0;
	private String a_sToExchange = null;
	private String a_sTypeExchange = null;
	private String a_sRoutingKey = null;
	private boolean a_bIsAuthenticate = false;
	private String a_sUserName = null;
	private String a_sUserPassword = null;

	private ConnectionFactory a_oRabbitMQConnectionFactory = null;
	private Channel a_oRabbitMQChannel = null;
	private com.rabbitmq.client.Connection a_oRabbitMQConnection = null;
	private Queue<Serializable> a_lFromQueue = null;

	public RabbitMQProducerConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	private byte[] Fx_Produce() throws Exception {
		if(a_lFromQueue == null) {
			return null;
		}
		Serializable oSerializable = a_lFromQueue.poll();
		if(TimeOutRoutine(oSerializable==null))
		{
			return null;
		}
		return Serializer.Serialize(oSerializable, 0);
	}

	public void SetFromQueue(Queue<Serializable> lFromQueue) {
		a_lFromQueue = lFromQueue;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sToHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADRESS);
		a_iThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sToExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sTypeExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGETYPE);
		a_sRoutingKey = oConfigurations.GetString(SETTINGS_KEY_ROUTINGKEY);
		a_nCheckTimeLowerBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND);
		a_nCheckTimeUpperBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND);
		a_nDeltaCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND);
		a_bIsAuthenticate = (boolean)oConfigurations.Get(SETTINGS_KEY_ISAUTHENTICATE);
		a_sUserName = oConfigurations.GetString(SETTINGS_KEY_USERNAME);
		a_sUserPassword = oConfigurations.GetString(SETTINGS_KEY_USERPASSWORD);

		a_nCheckTime = a_nCheckTimeLowerBound;
	}

	@Override
	public void Run() {
		try {
			a_oRabbitMQConnectionFactory = new ConnectionFactory();
			a_oRabbitMQConnectionFactory.setHost(a_sToHost);
			a_oRabbitMQConnectionFactory.setPort(a_iThroughPort);
			if(a_bIsAuthenticate) {
				a_oRabbitMQConnectionFactory.setUsername(a_sUserName);
				a_oRabbitMQConnectionFactory.setPassword(a_sUserPassword);
			}
			a_oRabbitMQConnection = a_oRabbitMQConnectionFactory.newConnection();
			a_oRabbitMQChannel = a_oRabbitMQConnection.createChannel();
			a_oRabbitMQChannel.exchangeDeclare(a_sToExchange, a_sTypeExchange);

			while(!IsClose()){
				byte[] rawBytes = Fx_Produce();
				if(rawBytes == null) { continue; }
				//TODO allowe variable routing key use IPackage
				a_oRabbitMQChannel.basicPublish(a_sToExchange, a_sRoutingKey, null, rawBytes);
			}
		} catch (Exception oException) {
			//TODO write errors to log queue
			System.out.printf("_WARNING: %s%s \"%s\"\n"
					,MLogManager.MethodName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.Connection;
import com.rabbitmq.client.*;

import java.io.*;
import java.util.Deque;
import java.util.UUID;

public class RabbitMQConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_EXCHANGE= "exchange";
	public static final String SETTINGS_KEY_EXCHANGETYPE = "exchange.type";
	public static final String SETTINGS_KEY_ROUTINGKEY = "routing.key";
	public static final String SETTINGS_KEY_ISHANDSHAKE = "ishandshake";
	public static final String SETTINGS_KEY_CHECKTIME = "checktime";
	public static final String SETTINGS_KEY_ISPACKAGEUNWRAP = "ispackageunwrap";
	public static final String SETTINGS_KEY_ISAUTHENTICATE = "isauthenticate";
	public static final String SETTINGS_KEY_USERNAME = "user.name";
	public static final String SETTINGS_KEY_USERPASSWORD = "user.password";

	private String a_sFromHost = null;
	private int a_iThroughPort = 0;
	private String a_sFromExchange = null;
	private String a_sTypeExchange = null;
	private String a_sFromQueue = null;
	private String a_sRoutingKey = null;
	private boolean a_bIsHandshake = true;
	private boolean a_bIsAuthenticate = false;
	private String a_sUserName = null;
	private String a_sUserPassword = null;
	private boolean a_bIsPackageUnwrap = false;

	protected TimeOut a_oTimeOut = null;
	protected Deque<Serializable> a_lToQueue = null;
	private ConnectionFactory a_oRabbitMQConnectionFactory = null;
	private Channel a_oRabbitMQChannel = null;
	private com.rabbitmq.client.Connection a_oRabbitMQConnection = null;

	public RabbitMQConsumerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	public void SetToQueue(Deque<Serializable> lToQueue) {
		a_lToQueue = lToQueue;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		if(a_lToQueue == null) { return; }
		Serializable ptrSerializable = Serializer.Deserialize(rawBytes,0);
		if(ptrSerializable == null) { return; }
		if(a_bIsPackageUnwrap) {
			IPackage oPackage = (IPackage)ptrSerializable;
			ptrSerializable =  oPackage.GetContent();
		}
		if(a_iRoute != null) {
			ptrSerializable = new Route(a_iRoute, ptrSerializable);
		}
		a_lToQueue.offerLast(ptrSerializable);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable sRouteId = oConfigurations.Get(SETTINGS_KEY_ROUTEID);
		a_iRoute = (sRouteId == null) ? null : UUID.fromString((String) sRouteId);
		a_sFromHost = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_iThroughPort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sFromExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sTypeExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGETYPE);
		a_sRoutingKey = oConfigurations.GetString(SETTINGS_KEY_ROUTINGKEY);
		a_bIsPackageUnwrap = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEUNWRAP);
		a_bIsHandshake = (boolean)oConfigurations.Get(SETTINGS_KEY_ISHANDSHAKE);
		a_bIsAuthenticate = (boolean)oConfigurations.Get(SETTINGS_KEY_ISAUTHENTICATE);
		a_sUserName = oConfigurations.GetString(SETTINGS_KEY_USERNAME);
		a_sUserPassword = oConfigurations.GetString(SETTINGS_KEY_USERPASSWORD);

		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIME));
	}

	@Override
	public void Run() {
		try {
			a_oRabbitMQConnectionFactory = new ConnectionFactory();
			a_oRabbitMQConnectionFactory.setHost(a_sFromHost);
			a_oRabbitMQConnectionFactory.setPort(a_iThroughPort);
			if(a_bIsAuthenticate) {
				a_oRabbitMQConnectionFactory.setUsername(a_sUserName);
				a_oRabbitMQConnectionFactory.setPassword(a_sUserPassword);
			}
			System.out.printf("_DEBUG: %s@%s { %s:%d, %s, %s, %s, %s, consumer }\n",MLogManager.MethodName()
					,GetIdentifier().toString(), a_sFromHost, a_iThroughPort, a_sUserName, a_sUserPassword, a_sFromExchange, a_sTypeExchange);
			a_oRabbitMQConnectionFactory.setAutomaticRecoveryEnabled(true);
			a_oRabbitMQConnection = a_oRabbitMQConnectionFactory.newConnection();
			a_oRabbitMQChannel = a_oRabbitMQConnection.createChannel();
			a_oRabbitMQChannel.exchangeDeclare(a_sFromExchange, a_sTypeExchange);
			a_sFromQueue = a_oRabbitMQChannel.queueDeclare().getQueue();
			a_oRabbitMQChannel.queueBind(a_sFromQueue, a_sFromExchange, a_sRoutingKey);
			a_oRabbitMQChannel.basicConsume(a_sFromQueue, a_bIsHandshake, oConsumer);
			// TODO stay active until connection closed or rabbitmq connection closed.
			while(!IsClose()) {
				a_oTimeOut.Routine(a_oRabbitMQConnection.isOpen());
			}
			a_oRabbitMQChannel.close();
			a_oRabbitMQConnection.close();
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}

	public Consumer oConsumer = new Consumer() {
		@Override
		public void handleConsumeOk(String s) {
			//TODO write message to log queue
			System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		}

		@Override
		public void handleCancelOk(String s) {
			//TODO write message to log queue
			System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		}

		@Override
		public void handleCancel(String s) throws IOException {
			//TODO write message to log queue
			System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		}

		@Override
		public void handleShutdownSignal(String s, ShutdownSignalException e) {
			//TODO write message to log queue
			System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		}

		@Override
		public void handleRecoverOk(String s) {
			//TODO write message to log queue
			System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
			try {
				if(body == null) { return; }
				Fx_Consume(body);
			} catch (Exception oException) {
				//TODO write errors to log queue
				System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
						,MLogManager.MethodName()
						,GetIdentifier().toString()
						,oException.getClass().getCanonicalName(),oException.getMessage());
			}
		}
	};
}

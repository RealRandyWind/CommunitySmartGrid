package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;
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
	public static final String SETTINGS_KEY_ISPACKAGEWRAPPED = "ispackagewrapped";
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
	private int a_nBufferCapacity = 0;
	private boolean a_bIsPackageWrapped = false;

	protected TimeOut a_oTimeOut = null;
	protected Queue<Serializable> a_lFromQueue = null;

	private ConnectionFactory a_oRabbitMQConnectionFactory = null;
	private Channel a_oRabbitMQChannel = null;
	private com.rabbitmq.client.Connection a_oRabbitMQConnection = null;

	public RabbitMQProducerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	private Serializable Fx_Produce() throws Exception {
		if(a_lFromQueue == null) { return null; }
		Serializable ptrSerializable = a_lFromQueue.poll();
		a_oTimeOut.Routine(ptrSerializable==null);
		return ptrSerializable;
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
		a_bIsPackageWrapped = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEWRAPPED);
		a_bIsAuthenticate = (boolean)oConfigurations.Get(SETTINGS_KEY_ISAUTHENTICATE);
		a_sUserName = oConfigurations.GetString(SETTINGS_KEY_USERNAME);
		a_sUserPassword = oConfigurations.GetString(SETTINGS_KEY_USERPASSWORD);

		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
		a_oTimeOut.SetUpperBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND));
		a_oTimeOut.SetDelta((int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND));
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
				Serializable ptrSerializable = Fx_Produce();
				if(ptrSerializable == null) { continue; }
				byte[] rawBytes = Serializer.Serialize(ptrSerializable, a_nBufferCapacity);
				if(rawBytes == null) { continue; }
				if(a_bIsPackageWrapped) {
					IPackage oPackage = (IPackage) ptrSerializable;
					UUID iRoute = oPackage.GetRoutIdentifier();
					String sRoutingKey = (iRoute == null ? a_sRoutingKey : iRoute.toString());
					a_oRabbitMQChannel.basicPublish(a_sToExchange, sRoutingKey, null, rawBytes);
				} else {
					a_oRabbitMQChannel.basicPublish(a_sToExchange, a_sRoutingKey, null, rawBytes);
				}
			}
		} catch (Exception oException) {
			//TODO write errors to log queue
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

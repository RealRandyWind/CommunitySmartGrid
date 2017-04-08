package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connection.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public abstract class AServerStub implements Runnable {
	protected MLogManager a_mLogManager = MLogManager.GetInstance();
	protected Thread a_oThread = null;
	protected TimeOut a_oTimeOut = null;
	private UUID a_oIdentifier = null;
	volatile protected boolean a_bIsStop = false;

	public AServerStub(UUID oIdentifier) {
		a_oThread = new Thread(this);
		a_oTimeOut = new TimeOut();
		a_oTimeOut.SetDelta(500);
		a_oTimeOut.SetLowerBound(5);
		a_oTimeOut.SetUpperBound(20000);
		a_oIdentifier = (oIdentifier == null ? UUID.randomUUID() : oIdentifier);
	}

	public void DisplayData(IData oData, String sContext) {
		Fx_DisplayData("data", sContext, oData);
	}

	public void DisplayResult(IData oData, String sContext) {
		Fx_DisplayData("result", sContext, oData);
	}

	private void Fx_DisplayData(String sSubject, String sContext, IData oData) {
		int iTuple = 0;
		Serializable[] ptrTuple = oData.GetTuple(iTuple);
		while(ptrTuple != null) {
			a_mLogManager.Debug("@%s %s(%d) %s \"%s\"",0
					,GetIdentifier().toString()
					,sSubject
					,iTuple
					,sContext
					,Arrays.toString(ptrTuple));
			++iTuple;
			ptrTuple =  oData.GetTuple(iTuple);
		}
	}

	public void DisplayAction(IAction oAction, String sContext) {
		a_mLogManager.Debug("@%s action(%s) %s",0,GetIdentifier().toString(),oAction.GetIdentifier().toString(), sContext);
	}

	public UUID GetIdentifier() {
		return a_oIdentifier;
	}

	public static final ISettings NewDataRealtimeConsumerSettingsRabbitMQ(String sRemote, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"data.realtime");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,false);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_CHECKTIME,200000);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewDataRealtimeConsumerSettingsUDP(String sLocal, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(UDPConsumerConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(UDPConsumerConnection.SETTINGS_KEY_BUFFERCAPACITY,64);
		oSettings.Set(UDPConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,false);
		oSettings.Set(UDPConsumerConnection.SETTINGS_KEY_LOCALADDRESS,sLocal);
		oSettings.Set(UDPConsumerConnection.SETTINGS_KEY_LOCALPORT,iPort);
		return oSettings;
	}

	public static final ISettings NewDataRealtimeConsumerSettingsTCP(String sLocal, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALADDRESS,sLocal);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_LOCALPORT,iPort);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_BUFFERCAPACITY,64);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,false);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(TCPConsumerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		return oSettings;
	}

	public static final ISettings NewActionControlConsumerSettings(String sRemote, int iPort, String iRoute, String iRouteKey, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGE,"action.control");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_EXCHANGETYPE,"direct");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISHANDSHAKE,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ISPACKAGEUNWRAP,true);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_CHECKTIME,200000);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_ROUTINGKEY,iRouteKey);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewDataRealtimeProducerSettings(String sRemote, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTEID, iRoute);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"data.realtime");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"fanout");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,false);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewActionControlProducerSettingsRabbitMQ(String sRemote, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGE,"action.control");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_EXCHANGETYPE,"direct");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISAUTHENTICATE,true);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,true);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(RabbitMQConsumerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_ROUTINGKEY,"");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERNAME,"guest");
		oSettings.Set(RabbitMQProducerConnection.SETTINGS_KEY_USERPASSWORD,"guest");
		return oSettings;
	}

	public static final ISettings NewActionControlProducerSettingsTCP(String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_ROUTEID, iRoute);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY, 64);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(TCPProducerConnection.SETTINGS_KEY_DELTACONNECTIONS, 2);
		return oSettings;
	}

	public static final ISettings NewActionControlProducerSettingsUDP(String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_ROUTEID, iRoute);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_BUFFERCAPACITY, 64);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(UDPProducerConnection.SETTINGS_KEY_DELTACONNECTIONS, 2);
		return oSettings;
	}

	public static final ISettings NewControllerListenerSettings(String sRemote, String sExchange, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RMIControllerListenerConnection.SETTINGS_KEY_EXCHANGE,sExchange);
		oSettings.Set(RMIControllerListenerConnection.SETTING_KEY_ISREBIND,false);
		oSettings.Set(RMIControllerListenerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,2000);
		oSettings.Set(RMIControllerListenerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RMIControllerListenerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RMIControllerListenerConnection.SETTING_KEY_ISFORCEUNEXPORT,true);
		return oSettings;
	}

	public static final ISettings NewControllerCallerSettings(String sRemote, String sExchange, int iPort, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(RMIControllerCallerConnection.SETTINGS_KEY_EXCHANGE,sExchange);
		oSettings.Set(RMIControllerCallerConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(RMIControllerCallerConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(RMIControllerCallerConnection.SETTING_KEY_ISREBIND,false);
		oSettings.Set(RMIControllerCallerConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,2000);
		return oSettings;
	}

	public static final ISettings NewResultStoreSettings(String sRemote, int iPort, String iRoute, UUID iSettings) {
		ISettings oSettings = new Settings(iSettings);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_ROUTEID,iRoute);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_ISPACKAGEWRAPPED,false);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_ISDOCUMENT,true);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_CHECKTIMELOWERBOUND,5);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_CHECKTIMEUPPERBOUND,20000);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_DELTACHECKUPPERBOUND,500);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_REMOTEADDRESS,sRemote);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_REMOTEPORT,iPort);
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_DATABASE,"8b9deeb8-ea9a-4a4e-93f3-a819b96c5620");
		oSettings.Set(MongoDBStoreConnection.SETTINGS_KEY_COLLECTION,"results");
		return oSettings;
	}
}

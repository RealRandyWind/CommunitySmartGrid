package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class TCPProducerConnection extends Connection {
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private Queue<Serializable> a_lFromQueue = null;
	private AbstractMap<Serializable, SocketAddress> a_lFromRemotesMap = null;

	private String a_sRemoteAddress = null;
	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nRemotePort = 0;
	private int a_nBufferCapacity = 0;

	private int a_nCheckTime = 0;
	private int a_nCheckTimeLowerBound = 0;
	private int a_nCheckTimeUpperBound = 0;
	private int a_nDeltaCheckTime = 0;

	public TCPProducerConnection(UUID oIdentifier,
								 Queue<Serializable> lFromQueue, Queue<Serializable> lToLogQueue,
								 AbstractMap<Serializable, SocketAddress> lFromRemotesMap) {
		super(oIdentifier, lToLogQueue);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: [TCPProducerConnection] no queue to produce from\n");
		}
		a_lFromQueue = lFromQueue;
		a_lFromRemotesMap = lFromRemotesMap;
	}

	private byte[] Fx_Produce() throws Exception {
		Serializable oSerializable = a_lFromQueue.poll();
		if (oSerializable==null){
			Thread.sleep(a_nCheckTime);
			a_nCheckTime += a_nDeltaCheckTime;
			a_nCheckTime = a_nCheckTime >= a_nCheckTimeUpperBound ? a_nCheckTimeUpperBound : a_nCheckTime;
			return null;
		}
		a_nCheckTime = a_nCheckTimeLowerBound;
		return Serializer.Serialize(oSerializable,a_nBufferCapacity);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_sLocalAddress = oConfigurations.GetString(SETTINGS_KEY_LOCALADDRESS);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);

		a_nCheckTimeLowerBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND);
		a_nCheckTimeUpperBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND);
		a_nDeltaCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND);

		a_nCheckTime = a_nCheckTimeLowerBound;
	}

	@Override
	public void Run() {
		System.out.printf("_WARNING: [TCPProducerConnection.Run] not yet implemented\n");
	}
}

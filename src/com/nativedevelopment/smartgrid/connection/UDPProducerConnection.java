package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Queue;
import java.util.UUID;

public class UDPProducerConnection extends Connection{
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private Queue<Serializable> a_lFromQueue = null;
	private Queue<Serializable> a_lToLogQueue = null;
	private Queue<SocketAddress> a_lReceivers = null;

	private DatagramChannel a_oDatagramChannel = null;

	private String a_sRemoteAddress = null;
	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nRemotePort = 0;
	private int a_nBufferCapacity = 0;

	private int a_nCheckTime = 0;
	private int a_nCheckTimeLowerBound = 0;
	private int a_nCheckTimeUpperBound = 0;
	private int a_nDeltaCheckTime = 0;

	public UDPProducerConnection(UUID oIdentifier, Queue<Serializable> lFromQueue, Queue<Serializable> lToLogQueue, Queue<SocketAddress> lReceivers) {
		super(oIdentifier);
		if(lFromQueue == null) {
			System.out.printf("_WARNING: [UDPProducerConnection] no queue to produce from\n");
		}
		a_lFromQueue = lFromQueue;
		a_lToLogQueue = lToLogQueue;
		a_lReceivers = lReceivers;
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
		a_sRemoteAddress = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_nRemotePort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
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
		try {
			SocketAddress oLocal = new InetSocketAddress(a_sLocalAddress, a_nLocalPort);

			a_oDatagramChannel = DatagramChannel.open();
			a_oDatagramChannel.bind(oLocal);
			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			while (!IsClose()) {
				oByteBuffer.clear();
				byte[] rawBytes = Fx_Produce();
				if(rawBytes == null) { continue; }
				oByteBuffer.put(rawBytes,0,rawBytes.length);

				for (SocketAddress oReceiver: a_lReceivers) {
					oByteBuffer.flip();
					a_oDatagramChannel.send(oByteBuffer, oReceiver);
				}
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: [UDPProducerConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

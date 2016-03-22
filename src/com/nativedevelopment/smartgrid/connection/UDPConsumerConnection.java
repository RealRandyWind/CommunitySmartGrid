package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.util.Queue;
import java.util.UUID;

public class UDPConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	private Queue<Serializable> a_lToQueue = null;
	private Queue<Serializable> a_lToLogQueue = null;

	private DatagramChannel a_oDatagramChannel = null;

	private String a_sRemoteAddress = null;
	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nRemotePort = 0;
	private int a_nBufferCapacity = 0;

	public UDPConsumerConnection(UUID oIdentifier, Queue<Serializable> lToQueue, Queue<Serializable> lToLogQueue) {
		super(oIdentifier);
		a_lToQueue = lToQueue;
		a_lToLogQueue = lToLogQueue;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		a_lToQueue.offer(Serializer.Deserialize(rawBytes,a_nBufferCapacity));
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sRemoteAddress = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_nRemotePort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_sLocalAddress = oConfigurations.GetString(SETTINGS_KEY_LOCALADDRESS);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);
	}

	@Override
	public void Run() {
		try {
			SocketAddress oRemote = new InetSocketAddress(a_sRemoteAddress, a_nRemotePort);
			SocketAddress oLocal = new InetSocketAddress(a_sLocalAddress, a_nLocalPort);

			a_oDatagramChannel = DatagramChannel.open();
			a_oDatagramChannel.bind(oLocal);
			a_oDatagramChannel.connect(oRemote);

			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			if (!a_oDatagramChannel.isConnected()) {
				System.out.printf("_WARNING: [UDPConsumerConnection.Run] can't connect %s\n",String.valueOf(oRemote));
				Close();
			}

			while (!IsClose()) {
				oByteBuffer.clear();
				a_oDatagramChannel.read(oByteBuffer);
				oByteBuffer.flip();
				byte[] rawBytes = new byte[oByteBuffer.remaining()];
				oByteBuffer.get(rawBytes,0,rawBytes.length);
				if(rawBytes == null) { continue; }
				Fx_Consume(rawBytes);
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: [UDPConsumerConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

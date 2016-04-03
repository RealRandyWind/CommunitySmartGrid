package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class UDPConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	private Queue<Serializable> a_lToQueue = null;
	private AbstractMap<Serializable, SocketAddress> a_lToRemotesMap = null;

	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nBufferCapacity = 0;

	public UDPConsumerConnection(UUID oIdentifier, Queue<Serializable> lToQueue, Queue<Serializable> lToLogQueue,
								 AbstractMap<Serializable, SocketAddress> lToRemotesMap) {
		super(oIdentifier, lToLogQueue);
		a_lToQueue = lToQueue;
		a_lToRemotesMap = lToRemotesMap;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		a_lToQueue.offer(Serializer.Deserialize(rawBytes,a_nBufferCapacity));
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_sLocalAddress = oConfigurations.GetString(SETTINGS_KEY_LOCALADDRESS);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);
	}

	@Override
	public void Run() {
		try {
			SocketAddress oLocal = new InetSocketAddress(a_sLocalAddress, a_nLocalPort);
			DatagramChannel a_oDatagramChannel = DatagramChannel.open();
			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			a_oDatagramChannel.bind(oLocal);

			while (!IsClose()) {
				oByteBuffer.clear();
				// TODO use channels to only allow connections that are expected
				SocketAddress oRemote = a_oDatagramChannel.receive(oByteBuffer);
				// TODO Tracks connections, use more reliable way to ensure uniqueness
				a_lToRemotesMap.putIfAbsent(oRemote.toString(),oRemote);
				oByteBuffer.flip();
				byte[] rawBytes = new byte[oByteBuffer.remaining()];
				oByteBuffer.get(rawBytes,0,rawBytes.length);
				if(rawBytes == null) { continue; }
				Fx_Consume(rawBytes);
			}

			a_oDatagramChannel.disconnect();
			a_oDatagramChannel.close();
		} catch (Exception oException) {
			System.out.printf("_WARNING: [UDPConsumerConnection.Run] %s \"%s\"\n"
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

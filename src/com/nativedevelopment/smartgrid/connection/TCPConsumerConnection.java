package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.UUID;

public class TCPConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	private Queue<Serializable> a_lToQueue = null;
	private Queue<Serializable> a_lToLogQueue = null;
	private AbstractMap<Object, SocketAddress> a_lRemotes = null;

	private SocketChannel a_oSocketChannel = null;

	private String a_sRemoteAddress = null;
	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nRemotePort = 0;
	private int a_nBufferCapacity = 0;

	public TCPConsumerConnection(UUID oIdentifier, Queue<Serializable> lToQueue, Queue<Serializable> lToLogQueue, AbstractMap<Object, SocketAddress> lRemotes) {
		super(oIdentifier);
		a_lToQueue = lToQueue;
		a_lToLogQueue = lToLogQueue;
		a_lRemotes = lRemotes;
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
		System.out.printf("_WARNING: [TCPConsumerConnection.Run] not yet implemented\n");
	}
}

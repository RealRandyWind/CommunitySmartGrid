package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.Serializable;
import java.net.SocketAddress;
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
	private AbstractMap<Serializable, SocketAddress> a_lToRemotesMap = null;

	private SocketChannel a_oSocketChannel = null;

	private String a_sRemoteAddress = null;
	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nRemotePort = 0;
	private int a_nBufferCapacity = 0;

	public TCPConsumerConnection(UUID oIdentifier, Queue<Serializable> lToQueue, Queue<Serializable> lToLogQueue, AbstractMap<Serializable, SocketAddress> lToRemotesMap) {
		super(oIdentifier);
		a_lToQueue = lToQueue;
		a_lToLogQueue = lToLogQueue;
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
		// TODO set up none blocking server socket channel and bind
		// TODO enter loop while not closing
		// TODO in loop check new connection request (check connection limit)
		// TODO in loop add channel to list, add its socket address to remotes
		// TODO in loop receive data from all listed channels (only block during a receive)
		// TODO exit loop if closing
		// TODO close channels remove channels from remotes
		// TODO close listener
		System.out.printf("_WARNING: [TCPConsumerConnection.Run] not yet implemented\n");
	}
}

package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class TCPConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nBufferCapacity = 0;

	protected Queue<Serializable> a_lToQueue = null;
	private Set<SocketChannel> a_lChannels = null;

	public TCPConsumerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_lChannels = new HashSet<>();
	}

	public void SetToQueue(Queue<Serializable> lToQueue) {
		a_lToQueue = lToQueue;
	}

	private void Fx_AcceptConnection(SocketChannel oChannel) throws Exception {
		//TimeOutRoutine()
		if((oChannel == null) || !oChannel.isConnected()) {
			return;
		}

		TimeOutRoutine(a_lChannels.isEmpty());

		//TODO use lRemotes to set up connection requested by queue
		oChannel.shutdownOutput();
		a_lChannels.add(oChannel);

		System.out.printf("_DEBUG: %sconnection accepted \"%s\" through \"%s\"\n"
				,MLogManager.MethodName()
				,String.valueOf(oChannel.getRemoteAddress())
				,String.valueOf(oChannel.getLocalAddress()));
	}

	private boolean Fx_CheckConnection(SocketChannel oChannel) throws Exception {
		if(oChannel.isConnected()) {
			return true;
		}
		a_lChannels.remove(oChannel);
		System.out.printf("_WARNING: %s lost connection \"%s\""
				,MLogManager.MethodName()
				,String.valueOf(oChannel.getRemoteAddress()));
		return false;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		if(a_lToQueue == null) {
			return;
		}
		a_lToQueue.offer(Serializer.Deserialize(rawBytes,a_nBufferCapacity));
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
		try {
			SocketAddress oLocal = new InetSocketAddress(a_sLocalAddress, a_nLocalPort);
			ServerSocketChannel oServerChannel = ServerSocketChannel.open();
			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			oServerChannel.bind(oLocal);
			oServerChannel.configureBlocking(false);

			while(!IsClose()) {
				Fx_AcceptConnection(oServerChannel.accept());
				for (SocketChannel oChannel: a_lChannels) {
					if(!Fx_CheckConnection(oChannel)) { continue; }
					oByteBuffer.clear();
					oChannel.read(oByteBuffer);
					// TODO problem sometimes receive multiple at once or temporary incomplete objects in byte buffer.
					oByteBuffer.flip();
					if(!oByteBuffer.hasRemaining()) { continue; }
					byte[] rawBytes = new byte[oByteBuffer.remaining()];
					oByteBuffer.get(rawBytes,0,rawBytes.length);
					Fx_Consume(rawBytes);
				}
			}

			oServerChannel.close();
			for (SocketChannel oChannel: a_lChannels) {
				oChannel.close();
			}
			a_lChannels.clear();
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s%s \"%s\"\n"
					,MLogManager.MethodName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

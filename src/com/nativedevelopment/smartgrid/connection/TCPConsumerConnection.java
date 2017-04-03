package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;

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
	public static final String SETTINGS_KEY_ISPACKAGEUNWRAP = "ispackageunwrap";
	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nBufferCapacity = 0;
	private boolean a_bIsPackageUnwrap = false;

	protected TimeOut a_oTimeOut = null;
	protected Queue<Serializable> a_lToQueue = null;
	private Set<SocketChannel> a_lChannels = null;

	public TCPConsumerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_lChannels = new HashSet<>();
		a_oTimeOut = new TimeOut();
	}

	public void SetToQueue(Queue<Serializable> lToQueue) {
		a_lToQueue = lToQueue;
	}

	private void Fx_AcceptConnection(SocketChannel oChannel) throws Exception {
		if((oChannel == null) || !oChannel.isConnected()) { return; }
		a_oTimeOut.Routine(a_lChannels.isEmpty());

		//TODO use lRemotes to set up connection requested by queue
		oChannel.shutdownOutput();
		a_lChannels.add(oChannel);

		System.out.printf("_DEBUG: %sconnection accepted \"%s\" through \"%s\"\n"
				,MLogManager.MethodName()
				,String.valueOf(oChannel.getRemoteAddress())
				,String.valueOf(oChannel.getLocalAddress()));
	}

	private boolean Fx_CheckConnection(SocketChannel oChannel) throws Exception {
		if(oChannel.isConnected()) { return true; }
		a_lChannels.remove(oChannel);
		System.out.printf("_WARNING: %s lost connection \"%s\""
				,MLogManager.MethodName()
				,String.valueOf(oChannel.getRemoteAddress()));
		return false;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		if(a_lToQueue == null) { return; }
		Serializable ptrSerializable = Serializer.Deserialize(rawBytes,0);
		if(ptrSerializable == null) { return; }
		if(a_bIsPackageUnwrap) {
			IPackage oPackage = (IPackage)ptrSerializable;
			a_lToQueue.offer(oPackage.GetContent());
		} else {
			a_lToQueue.offer(ptrSerializable);
		}
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_sLocalAddress = oConfigurations.GetString(SETTINGS_KEY_LOCALADDRESS);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);
		a_bIsPackageUnwrap = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEUNWRAP);
		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
		a_oTimeOut.SetUpperBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND));
		a_oTimeOut.SetDelta((int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND));
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
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
	/*
	private class _Connection {
		private SocketChannel a_oChannel = null;
		private ByteBuffer a_oByteBuffer = null;

		public _Connection(SocketChannel oChannel, int nBufferCapacity) {
			a_oChannel = oChannel;
			a_oByteBuffer = ByteBuffer.allocate(nBufferCapacity);
		}

		public ByteBuffer GetBuffer() {
			return a_oByteBuffer;
		}

		public SocketChannel GetChannel() {
			return a_oChannel;
		}

		public boolean equals(Object o) {
		}
	}
	*/
}

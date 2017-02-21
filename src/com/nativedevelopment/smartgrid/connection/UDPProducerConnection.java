package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Serializer;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class UDPProducerConnection extends Connection{
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";
	public static final String SETTINGS_KEY_CHECKTIMEUPPERBOUND = "checktime.upperbound";
	public static final String SETTINGS_KEY_DELTACHECKUPPERBOUND = "checktime.delta";

	public static final String SETTINGS_KEY_DELTACONNECTIONS = "connections.delta";

	private int a_nBufferCapacity = 0;
	private int a_nDeltaConnections = 0;

	private Set<DatagramChannel> a_lChannels = null;
	private Queue<Serializable> a_lRemoteQueue = null;
	private Queue<Serializable> a_lFromQueue = null;

	public UDPProducerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_lChannels = new HashSet<>();
	}

	public void SetRemoteQueue(Queue<Serializable> lRemoteQueue) {
		a_lRemoteQueue = lRemoteQueue;
	}

	public void SetFromQueue(Queue<Serializable> lFromQueue) {
		a_lFromQueue = lFromQueue;
	}

	private void Fx_EstablishConnections() throws Exception {
		Serializable ptrRemote =  a_lRemoteQueue.poll();
		int iRemote = a_nDeltaConnections;
		while (ptrRemote!=null && 0 < iRemote) {
			SocketAddress oRemote = (SocketAddress) ptrRemote;
			DatagramChannel oChannel = DatagramChannel.open();
			oChannel.connect(oRemote);
			a_lChannels.add(oChannel);
			ptrRemote = a_lRemoteQueue.poll();
			iRemote--;
			System.out.printf("_DEBUG: %snew connection to \"%s\" through \"%s\"\n"
					,MLogManager.MethodName()
					,String.valueOf(oChannel.getRemoteAddress())
					,String.valueOf(oChannel.getLocalAddress()));
		}
	}

	private boolean Fx_CheckConnection(DatagramChannel oChannel) throws Exception {
		if(oChannel.isConnected()) {
			return true;
		}
		a_lChannels.remove(oChannel);
		System.out.printf("_WARNING: %slost connection \"%s\"\n"
				,MLogManager.MethodName()
				,String.valueOf(oChannel.getRemoteAddress()));
		return false;
	}

	private byte[] Fx_Produce() throws Exception {
		if(a_lFromQueue == null) {
			return null;
		}
		Serializable oSerializable = a_lFromQueue.poll();
		if(TimeOutRoutine(oSerializable==null)) {
			return null;
		}
		return Serializer.Serialize(oSerializable,a_nBufferCapacity);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_nCheckTimeLowerBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND);
		a_nCheckTimeUpperBound = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMEUPPERBOUND);
		a_nDeltaCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_DELTACHECKUPPERBOUND);
		a_nDeltaConnections = (int)oConfigurations.Get(SETTINGS_KEY_DELTACONNECTIONS);
		
		a_nCheckTime = a_nCheckTimeLowerBound;
	}

	@Override
	public void Run() {
		try {
			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			while (!IsClose()) {
				Fx_EstablishConnections();

				byte[] rawBytes = Fx_Produce();
				if(rawBytes == null) { continue; }
				oByteBuffer.clear();
				oByteBuffer.put(rawBytes,0,rawBytes.length);

				for (DatagramChannel oChannel: a_lChannels) {
					if(!Fx_CheckConnection(oChannel)) { continue; }
					oByteBuffer.flip();
					oChannel.write(oByteBuffer);
				}
			}

			for (DatagramChannel oChannel: a_lChannels) {
				oChannel.disconnect();
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

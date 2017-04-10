package com.nativedevelopment.smartgrid.connections;

import com.nativedevelopment.smartgrid.*;

import java.io.Serializable;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class UDPConsumerConnection extends Connection {
	public static final String SETTINGS_KEY_LOCALADDRESS = "local.address";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTINGS_KEY_BUFFERCAPACITY = "buffer.capacity";
	public static final String SETTINGS_KEY_ISPACKAGEUNWRAP = "ispackageunwrap";

	private String a_sLocalAddress = null;
	private int a_nLocalPort = 0;
	private int a_nBufferCapacity = 0;
	private boolean a_bIsPackageUnwrap = false;
	private String a_sRoutingKey = null;

	protected Deque<Serializable> a_lToQueue = null;

	public UDPConsumerConnection(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void SetToQueue(Deque<Serializable> lToQueue) {
		a_lToQueue = lToQueue;
	}

	public void SetRoutingKey(String sRoutingKey) {
		a_sRoutingKey = sRoutingKey;
	}

	private void Fx_Consume(byte[] rawBytes) throws Exception {
		if(a_lToQueue == null) { return; }
		Serializable ptrSerializable = Serializer.Deserialize(rawBytes,0);
		if(ptrSerializable == null) { return; }
		if(a_bIsPackageUnwrap) {
			IPackage oPackage = (IPackage)ptrSerializable;
			ptrSerializable =  oPackage.GetContent();
		}
		if(a_iRoute != null) {
			ptrSerializable = new Route(a_iRoute, ptrSerializable);
		}
		a_lToQueue.offerLast(ptrSerializable);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		Serializable sRouteId = oConfigurations.Get(SETTINGS_KEY_ROUTEID);
		a_iRoute = (sRouteId == null) ? null : UUID.fromString((String) sRouteId);
		a_bIsPackageUnwrap = (boolean)oConfigurations.Get(SETTINGS_KEY_ISPACKAGEUNWRAP);
		a_nBufferCapacity = (int)oConfigurations.Get(SETTINGS_KEY_BUFFERCAPACITY);
		a_sLocalAddress = oConfigurations.GetString(SETTINGS_KEY_LOCALADDRESS);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);
	}

	@Override
	public void Run() {
		try {
			SocketAddress oLocal = new InetSocketAddress(a_sLocalAddress, a_nLocalPort);
			DatagramChannel oServerChannel = DatagramChannel.open();
			ByteBuffer oByteBuffer = ByteBuffer.allocate(a_nBufferCapacity);

			oServerChannel.bind(oLocal);

			while (!IsClose()) {
				oByteBuffer.clear();
				oServerChannel.receive(oByteBuffer);
				oByteBuffer.flip();
				if(!oByteBuffer.hasRemaining()) { continue; }
				byte[] rawBytes = new byte[oByteBuffer.remaining()];
				oByteBuffer.get(rawBytes,0,rawBytes.length);
				Fx_Consume(rawBytes);
			}

			oServerChannel.disconnect();
			oServerChannel.close();
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

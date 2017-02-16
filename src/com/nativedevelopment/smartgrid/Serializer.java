package com.nativedevelopment.smartgrid;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Serializer {
	private Serializer(){

	}

	public static Serializable Deserialize(byte[] rawBytes, int nBufferCapacity) throws Exception {
		ByteArrayInputStream oByteArray = new ByteArrayInputStream(rawBytes);
		ObjectInput oObjectInput = new ObjectInputStream(oByteArray);
		Serializable ptrSerializable = (Serializable)oObjectInput.readObject();
		oObjectInput.close();
		return ptrSerializable;
	}

	public static byte[] Serialize(Serializable oSerializable, int nBufferCapacity) throws Exception {
		ByteArrayOutputStream oByteArray = new ByteArrayOutputStream(nBufferCapacity);
		ObjectOutput oObjectOutput = new ObjectOutputStream(oByteArray);
		oObjectOutput.writeObject(oSerializable);
		oObjectOutput.flush();
		byte[] rawBytes = oByteArray.toByteArray();
		oByteArray.close();
		return rawBytes;
	}
}

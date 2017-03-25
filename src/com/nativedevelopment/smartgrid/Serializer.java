package com.nativedevelopment.smartgrid;

import java.io.*;

public final class Serializer {
	private Serializer(){

	}

	public static Serializable Deserialize(ByteArrayInputStream oByteArray) throws Exception {
		ObjectInput oObjectInput = new ObjectInputStream(oByteArray);
		Serializable ptrSerializable = (Serializable)oObjectInput.readObject();
		// oObjectInput.close();
		return ptrSerializable;
	}

	public static Serializable Deserialize(byte[] rawBytes, int nBufferCapacity) throws Exception {
		ByteArrayInputStream oByteArray = new ByteArrayInputStream(rawBytes);
		ObjectInput oObjectInput = new ObjectInputStream(oByteArray);
		Serializable ptrSerializable = (Serializable)oObjectInput.readObject();
		oObjectInput.close();
		return ptrSerializable;
	}

	public static byte[] Serialize(Serializable ptrSerializable, int nBufferCapacity) throws Exception {
		ByteArrayOutputStream oByteArray = new ByteArrayOutputStream(nBufferCapacity);
		ObjectOutput oObjectOutput = new ObjectOutputStream(oByteArray);
		oObjectOutput.writeObject(ptrSerializable);
		oObjectOutput.flush();
		byte[] rawBytes = oByteArray.toByteArray();
		oByteArray.close();
		return rawBytes;
	}
}

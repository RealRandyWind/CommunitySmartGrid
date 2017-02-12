package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Serializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Gebruiker on 11/02/2017.
 */
public class SerializerTest implements ITestCase {
	MLogManager a_mLogManager = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();

		a_mLogManager.SetUp();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testSerializeDeserialize() throws Exception {
		a_mLogManager.Test("begin",0);
		Serializable oSerializable1 = new String("Serializable1");
		Serializable oSerializable2 = new SerializableObject("SerializableObject2");

		byte[] rawBytes1 = Serializer.Serialize(oSerializable1,0);
		byte[] rawBytes2 = Serializer.Serialize(oSerializable2,0);

		assertEquals(Serializer.Deserialize(rawBytes1,0), oSerializable1);
		assertEquals(Serializer.Deserialize(rawBytes2,0), oSerializable2);
		a_mLogManager.Test("end",0);
	}
}
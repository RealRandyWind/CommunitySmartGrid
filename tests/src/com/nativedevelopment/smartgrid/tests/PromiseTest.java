package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IPromise;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Promise;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class PromiseTest implements ITestCase {
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
	public void testSetGetIsDoneIsChanged() throws Exception {
		a_mLogManager.Test("begin",0);
		Serializable oSerializable1 = new SerializableObject("SerializableObject1");
		Serializable oSerializable2 = new SerializableObject("SerializableObject2");
		IPromise oPromise = new Promise();

		assertFalse(oPromise.IsDone());
		assertFalse(oPromise.IsChanged());
		assertNull(oPromise.Get());
		assertFalse(oPromise.IsChanged());
		oPromise.Set(oSerializable1);
		assertTrue(oPromise.IsDone());
		assertTrue(oPromise.IsChanged());
		assertEquals(oSerializable1,oPromise.Get());
		assertFalse(oPromise.IsChanged());
		assertTrue(oPromise.IsDone());
		oPromise.Set(oSerializable2);
		assertTrue(oPromise.IsChanged());
		assertTrue(oPromise.IsDone());
		assertNotEquals(oSerializable1,oPromise.Get());
		assertFalse(oPromise.IsChanged());
		assertTrue(oPromise.IsDone());
		assertEquals(oSerializable2,oPromise.Get());
		oPromise.Set(null);
		assertFalse(oPromise.IsDone());
		assertTrue(oPromise.IsChanged());
		assertNull(oPromise.Get());
		assertFalse(oPromise.IsChanged());
		a_mLogManager.Test("end",0);
	}
}
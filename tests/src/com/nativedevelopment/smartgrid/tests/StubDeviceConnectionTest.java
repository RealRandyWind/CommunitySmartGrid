package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class StubDeviceConnectionTest implements ITestCase {
	public static final int NUM_ACTIONS = 20;
	MLogManager a_mLogManager = null;

	UUID a_iAction = null;
	UUID a_iDevice = null;
	String[] a_lAttributes = null;
	AbstractMap<UUID,Serializable> a_lActionMap = null;
	Queue<Serializable> a_lToQueue = null;
	Queue<Serializable> a_lFromQueue = null;
	Queue<Serializable> a_lToLogQueue = null;
	Queue<Serializable> a_lQueue = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();

		a_mLogManager.SetUp();

		a_lActionMap = new ConcurrentHashMap<>();
		a_lToQueue = new ConcurrentLinkedQueue<>();
		a_lFromQueue = new ConcurrentLinkedQueue<>();;
		a_lToLogQueue = new ConcurrentLinkedQueue<>();;
		a_lQueue = new ConcurrentLinkedQueue<>();;

		a_iAction = UUID.randomUUID();
		a_iDevice = UUID.randomUUID();
		a_lAttributes = new String[3];

		a_lAttributes[0] = "timestamp";
		a_lAttributes[1] = "interval";
		a_lAttributes[2] = "activity";

		for (int iIndex = 0; iIndex < NUM_ACTIONS; iIndex++) {
			a_lActionMap.put(UUID.randomUUID(),iIndex);
		}
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("begin",0);
		IConnection oConnection = new StubDeviceConnection(null, a_iDevice, a_lAttributes, a_lActionMap, a_lQueue);

		oConnection.SetToLogQueue(a_lToLogQueue);
		oConnection.SetFromQueue(a_lFromQueue);
		oConnection.SetToQueue(a_lToQueue);

		assertTrue(a_lToQueue.isEmpty());
		a_mLogManager.Test("Open",0);
		oConnection.Open();
		Thread.sleep(100);

		a_mLogManager.Test("Run",0);
		for (UUID iAction: a_lActionMap.keySet()) {
			a_lFromQueue.offer(new Action(iAction,null));
			Thread.sleep(200);
		}
		a_lFromQueue.offer(new Action(a_iAction,null));
		Thread.sleep(4000);

		a_mLogManager.Test("Close",0);
		oConnection.Close();
		Thread.sleep(200);

		assertFalse(a_lToQueue.isEmpty());
		assertTrue(a_lFromQueue.isEmpty());

		for (UUID iAction: a_lActionMap.keySet()) {
			assertTrue(a_lQueue.contains(iAction));
		}

		assertFalse(a_lQueue.contains(a_iAction));

		for (Serializable oSerializable: a_lToQueue) {
			IData oData = (IData)oSerializable;
			assertEquals(a_iDevice,oData.GetIdentifier());
		}

		a_mLogManager.Test("end",0);
	}
}
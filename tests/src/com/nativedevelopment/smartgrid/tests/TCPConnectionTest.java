package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class TCPConnectionTest implements ITestCase {
	MLogManager a_mLogManager = null;

	Queue<Serializable> a_oFromQueue = null;
	Queue<Serializable> a_oToQueue = null;
	Queue<Serializable> a_oLogQueue = null;
	Queue<SocketAddress> a_lReceivers = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mLogManager.SetUp();

		a_oFromQueue = new ConcurrentLinkedQueue<>();
		a_oToQueue = new ConcurrentLinkedQueue<>();
		a_oLogQueue = new ConcurrentLinkedQueue<>();
		a_lReceivers = new ConcurrentLinkedQueue<>();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		a_mLogManager.Test("[TCPConnectionTest.testRun] begin",0);

		a_mLogManager.Test("[TCPConnectionTest.testRun] Configure",0);
		a_mLogManager.Test("[TCPConnectionTest.testRun] Open",0);
		Thread.sleep(500);
		a_mLogManager.Test("[TCPConnectionTest.testRun] Run",0);
		Thread.sleep(100);
		a_mLogManager.Test("[TCPConnectionTest.testRun] Close",0);
		Thread.sleep(2000);

		a_mLogManager.Test("[TCPConnectionTest.testRun] end",0);
	}
}
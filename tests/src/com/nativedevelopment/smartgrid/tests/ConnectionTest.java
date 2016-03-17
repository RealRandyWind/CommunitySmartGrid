package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IConnection;
import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ConnectionTest implements ITestCase {
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
	public void testGetIdentifier() throws Exception {
		a_mLogManager.Test("[ConnectionTest.testGetIdentifier] begin",0);
		UUID iConnection2 = UUID.randomUUID();
		IConnection oConnection1 = new Connection(null);
		IConnection oConnection2 = new Connection(iConnection2);

		assertNotNull(oConnection1.GetIdentifier());
		assertEquals(iConnection2,oConnection2.GetIdentifier());
		a_mLogManager.Test("[ConnectionTest.testGetIdentifier] end",0);
	}

	@Test
	public void testOpenIsActiveCloseForceClose() throws Exception {
		a_mLogManager.Test("[ConnectionTest.testOpenIsActiveClose] begin",0);
		IConnection oConnection = new Connection(null);
		assertFalse(oConnection.IsActive());
		oConnection.Open();
		assertTrue(oConnection.IsActive());
		// TODO test close
		oConnection.ForceClose();
		Thread.sleep(2000);
		assertFalse(oConnection.IsActive());
		a_mLogManager.Test("[ConnectionTest.testOpenIsActiveClose] end",0);
	}
}
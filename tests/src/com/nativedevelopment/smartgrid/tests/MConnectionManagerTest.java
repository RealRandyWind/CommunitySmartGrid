package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IConnection;
import com.nativedevelopment.smartgrid.MConnectionManager;
import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MConnectionManagerTest implements ITestCase {
	MLogManager a_mLogManager = null;
	MConnectionManager a_mConnectionManager = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mConnectionManager = MConnectionManager.GetInstance();
		a_mLogManager.SetUp();
		a_mConnectionManager.SetUp();
	}

	@After
	public void tearDown() throws Exception {
		a_mConnectionManager.ShutDown();
		a_mLogManager.ShutDown();
	}

	@Test
	public void testAddGetGetAllRemoveConnection() throws Exception {
		a_mLogManager.Test("begin",0);
		IConnection oConnection1 = new Connection(null,null);
		IConnection oConnection2 = new Connection(null,null);
		IConnection oConnection3 = new Connection(null,null);

		a_mLogManager.Test("Get and Add",0);
		assertNull(a_mConnectionManager.GetConnection(oConnection1.GetIdentifier()));
		a_mConnectionManager.AddConnection(oConnection1);
		assertSame(oConnection1,a_mConnectionManager.GetConnection(oConnection1.GetIdentifier()));

		assertNull(a_mConnectionManager.GetConnection(oConnection2.GetIdentifier()));
		a_mConnectionManager.AddConnection(oConnection2);
		assertSame(oConnection2,a_mConnectionManager.GetConnection(oConnection2.GetIdentifier()));

		assertNull(a_mConnectionManager.GetConnection(oConnection3.GetIdentifier()));
		a_mConnectionManager.AddConnection(oConnection3);
		assertSame(oConnection3,a_mConnectionManager.GetConnection(oConnection3.GetIdentifier()));

		a_mLogManager.Test("GetAll",0);
		int nCount = 0;
		Iterable<IConnection> lConnections = a_mConnectionManager.GetAllConnections();
		for (IConnection oConnection : lConnections ) {
			assertNotNull(oConnection);
			nCount++;
		}
		assertTrue(nCount==3);

		a_mLogManager.Test("Remove",0);
		assertSame(oConnection2,a_mConnectionManager.GetConnection(oConnection2.GetIdentifier()));
		a_mConnectionManager.RemoveConnection(oConnection2.GetIdentifier());
		assertNull(a_mConnectionManager.GetConnection(oConnection2.GetIdentifier()));
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testDisestablishEstablishConnection() throws Exception {
		a_mLogManager.Test("begin",0);
		IConnection oConnection = new Connection(null,null);
		a_mConnectionManager.AddConnection(oConnection);

		a_mLogManager.Test("Establish",0);
		assertFalse(oConnection.IsActive());
		a_mConnectionManager.EstablishConnection(oConnection.GetIdentifier());
		assertTrue(oConnection.IsActive());

		a_mLogManager.Test("Disestablish",0);
		a_mConnectionManager.DisestablishConnection(oConnection.GetIdentifier());
		Thread.sleep(2000);
		assertFalse(oConnection.IsActive());
		a_mLogManager.Test("end",0);
	}
}
package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest implements ITestCase {
	Client a_oApplication = null;
	MLogManager a_mLogManager = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (Client)Client.GetInstance();
		a_oApplication.SetUp();

		a_mLogManager = MLogManager.GetInstance();
	}

	@After
	public void tearDown() throws Exception {
		a_oApplication.ShutDown();
	}

	@Test
	public void testLoadSettingsFromFileFromString() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testGetGetAllRegisterUnregisterDevice() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishActionControlConnection() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishDensDataConnection() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishRealtimeDataConnection() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishControllerConnection() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishSubscriptionConnection() throws Exception {
		fail("not yet implemented.");
	}

	@Test
	public void testEstablishDeviceConnection() throws Exception {
		fail("not yet implemented.");
	}
}
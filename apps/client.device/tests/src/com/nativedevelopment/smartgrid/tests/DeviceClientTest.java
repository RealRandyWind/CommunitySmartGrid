package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.client.device.DeviceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceClientTest implements ITestCase {
	DeviceClient a_oApplication = null;
	MLogManager a_mLogManager = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (DeviceClient)DeviceClient.GetInstance();
		a_oApplication.SetUp();

		a_mLogManager = MLogManager.GetInstance();
	}

	@After
	public void tearDown() throws Exception {
		a_oApplication.ShutDown();
	}

	@Test
	public void testRun() throws Exception {
		fail("not yet implemented.");
	}
}
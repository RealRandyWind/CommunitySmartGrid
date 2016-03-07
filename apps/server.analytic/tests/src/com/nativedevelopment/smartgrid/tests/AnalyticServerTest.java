package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Main;
import com.nativedevelopment.smartgrid.server.analytic.AnalyticServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnalyticServerTest implements ITestCase {
	AnalyticServer a_oApplication = null;
	MLogManager a_mLogManager = null;

	@Before
	public void setUp() throws Exception {
		a_oApplication = (AnalyticServer)AnalyticServer.GetInstance();
		a_oApplication.SetUp();

		a_mLogManager = MLogManager.GetInstance();
	}

	@After
	public void tearDown() throws Exception {
		a_oApplication.ShutDown();
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
	public void testEstablishResultsConnection() throws Exception {
		fail("not yet implemented.");
	}
}
package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class ActionTest implements ITestCase {
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
		a_mLogManager.Test("begin",0);
		UUID iAction2 = UUID.randomUUID();
		Serializable[] lParameters = new Serializable[3];
		lParameters[0] = "normal";
		lParameters[1] = 22;
		lParameters[2] = 0.234567;
		IAction oAction1 = new Action(null,lParameters);
		IAction oAction2 = new Action(iAction2,lParameters);

		assertNotNull(oAction1.GetIdentifier());
		assertEquals(iAction2,oAction2.GetIdentifier());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGetParameters() throws Exception {
		a_mLogManager.Test("[ActionTest.testGetParameters] begin",0);
		Serializable[] lParameters = new Serializable[3];
		lParameters[0] = "normal";
		lParameters[1] = 22;
		lParameters[2] = 0.234567;
		IAction oAction = new Action(null,lParameters);

		assertArrayEquals(lParameters,oAction.GetParameters());
		a_mLogManager.Test("[ActionTest.testGetParameters] end",0);
	}
}
package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.client.Device;
import com.nativedevelopment.smartgrid.client.IDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class DeviceTest implements ITestCase {
	MLogManager a_mLogManager = null;

	String[] a_lDataSPace = new String[9];
	UUID[] a_lActionSpace = new UUID[5];

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();

		a_mLogManager.SetUp();

		a_lDataSPace[0] = "identifier";
		a_lDataSPace[1] = "latitude";
		a_lDataSPace[2] = "longitude";
		a_lDataSPace[3] = "timestamp";
		a_lDataSPace[4] = "interval";
		a_lDataSPace[5] = "activity";
		a_lDataSPace[6] = "x";
		a_lDataSPace[7] = "y";
		a_lDataSPace[8] = "z";

		a_lActionSpace[0] = UUID.randomUUID();
		a_lActionSpace[1] = UUID.randomUUID();
		a_lActionSpace[2] = UUID.randomUUID();
		a_lActionSpace[3] = UUID.randomUUID();
		a_lActionSpace[4] = UUID.randomUUID();
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testGetIdentifier() throws Exception {
		a_mLogManager.Test("begin",0);
		UUID iDevice2 = UUID.randomUUID();
		IDevice oDevice1 = new Device(null,a_lDataSPace,a_lActionSpace);
		IDevice oDevice2 = new Device(iDevice2,a_lDataSPace,a_lActionSpace);

		assertNotNull(oDevice1.GetIdentifier());
		assertEquals(iDevice2,oDevice2.GetIdentifier());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGetDataSpace() throws Exception {
		a_mLogManager.Test("begin",0);
		IDevice oDevice = new Device(null,a_lDataSPace,a_lActionSpace);
		assertArrayEquals(a_lDataSPace,oDevice.GetDataSpace());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGetActionSpace() throws Exception {
		a_mLogManager.Test("begin",0);
		IDevice oDevice = new Device(null,a_lDataSPace,a_lActionSpace);
		assertArrayEquals(a_lActionSpace,oDevice.GetActionSpace());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testControl() throws Exception {
		a_mLogManager.Test("begin",0);
		IDevice oDevice = new Device(null,a_lDataSPace,a_lActionSpace);
		Serializable[] lParameters = new Serializable[3];
		lParameters[0] = "normal";
		lParameters[1] = 22;
		lParameters[2] = 0.234567;
		IAction oAction = new Action(a_lActionSpace[0],lParameters);
		oDevice.Control(oAction);
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testSetIsAllowControl() throws Exception {
		a_mLogManager.Test("begin",0);
		IDevice oDevice = new Device(null,a_lDataSPace,a_lActionSpace);

		a_mLogManager.Test("IsAllowControl",0);
		assertFalse(oDevice.IsAllowControl());

		a_mLogManager.Test("SetAllowControl",0);
		oDevice.SetAllowControl(true);
		assertTrue(oDevice.IsAllowControl());
		oDevice.SetAllowControl(false);
		assertFalse(oDevice.IsAllowControl());
		a_mLogManager.Test("end",0);
	}
}
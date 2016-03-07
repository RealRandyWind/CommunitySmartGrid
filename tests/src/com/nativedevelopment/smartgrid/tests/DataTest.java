package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IData;
import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.jar.Attributes;

import static org.junit.Assert.*;

public class DataTest implements ITestCase {
	MLogManager a_mLogManager = null;

	String[] a_lAttributes = new String[3];
	Serializable[][] a_lTuples = new Serializable[3][3];

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();

		a_mLogManager.SetUp();

		a_lAttributes[0] = "timestamp";
		a_lAttributes[1] = "interval";
		a_lAttributes[2] = "activity";

		a_lTuples[0][0] = System.currentTimeMillis();
		a_lTuples[0][1] = 3000;
		a_lTuples[0][2] = 122.87;

		a_lTuples[1][0] = System.currentTimeMillis() + 5000;
		a_lTuples[1][1] = 6000;
		a_lTuples[1][2] = 338.23;

		a_lTuples[2][0] = System.currentTimeMillis() + 2000;
		a_lTuples[2][1] = 1250;
		a_lTuples[2][2] = 34.85;
	}

	@After
	public void tearDown() throws Exception {
		a_mLogManager.ShutDown();
	}

	@Test
	public void testGetAttributes() throws Exception {
		a_mLogManager.Test("[DataTest.testGetAttributes] begin",0);
		IData oData = new Data(a_lTuples,a_lAttributes);

		assertArrayEquals(a_lAttributes,oData.GetAttributes());
		a_mLogManager.Test("[DataTest.testGetAttributes] end",0);
	}

	@Test
	public void testGetGetAllTuple() throws Exception {
		a_mLogManager.Test("[DataTest.testGetGetAllTuple] begin",0);
		IData oData = new Data(a_lTuples,a_lAttributes);

		assertArrayEquals(a_lTuples,oData.GetAllTuples());
		assertArrayEquals(a_lTuples[0],oData.GetTuple(0));
		assertArrayEquals(a_lTuples[1],oData.GetTuple(1));
		assertArrayEquals(a_lTuples[2],oData.GetTuple(2));
		assertNull(oData.GetTuple(3));
		a_mLogManager.Test("[DataTest.testGetGetAllTuple] end",0);
	}
}
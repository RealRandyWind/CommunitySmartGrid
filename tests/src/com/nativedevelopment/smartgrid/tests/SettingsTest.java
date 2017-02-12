package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class SettingsTest implements ITestCase {
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
		UUID iSettings2 = UUID.randomUUID();
		ISettings oSettings1 = new Settings(null);
		ISettings oSettings2 = new Settings(iSettings2);

		assertNotNull(oSettings1.GetIdentifier());
		assertEquals(iSettings2,oSettings2.GetIdentifier());
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testSetSetSpecialGetGetAllGetString() throws Exception {
		a_mLogManager.Test("begin",0);
		String sValueString1 = "\"serializable\"";
		String sValueString2 = "serializable";
		String sValueNoString1 ="a\"serializable\"";

		int nInteger1 = 1234;
		int nInteger2 = -1234;
		String sValueInteger1 ="1234";
		String sValueInteger2 ="1234 ";
		String sValueInteger3 ="-1234";
		String sValueNoInteger1 =".1234";
		String sValueNoInteger2 ="1234.";
		String sValueNoInteger3 ="\"1234\"";

		double dReal1 = 1234;
		double dReal2 = .1234;
		double dReal3 = 1234.1234;
		double dReal4 = -.1234;
		double dReal5 = -1234.1234;
		String sValueReal1 =".1234";
		String sValueReal2 ="1234.";
		String sValueReal3 ="1234.1234";
		String sValueReal4 ="1234.1234 ";
		String sValueReal5 ="-1234.1234";
		String sValueReal6 ="-.1234";
		String sValueNoReal1 =".1234.1234";
		String sValueNoReal2 ="\"1234\"";
		String sValueNoReal3 ="\".1234\"";
		String sValueNoReal4 ="\"1234.\"";
		String sValueNoReal5 ="\"1234.1234\"";

		boolean bBoolean1 = true;
		boolean bBoolean2 = false;
		String sValueBoolean1 ="true";
		String sValueBoolean2 ="false";
		String sValueBoolean3 ="false ";
		String sValueNoBoolean1 ="atrue";
		String sValueNoBoolean2 ="\"true\"";
		String sValueNoBoolean3 ="\"false\"";

		String sValueNull = "null";
		String sValueNoNull = "\"null\"";

		ISettings oSettings1 = new Settings(null);

		a_mLogManager.Test("Set and Get",0);
		assertEquals("null",oSettings1.GetString("testit"));
		oSettings1.Set("testit",sValueString2);
		assertEquals(sValueString2,oSettings1.GetString("testit"));

		assertNull(oSettings1.Get("testit2"));
		oSettings1.Set("testit2",sValueString2);
		assertEquals(sValueString2,oSettings1.Get("testit2"));

		assertNull(oSettings1.Get("testit3"));
		oSettings1.Set("testit3",sValueString2);
		assertEquals(sValueString2,oSettings1.Get("testit3"));

		a_mLogManager.Test("GetAll",0);
		Iterable<Map.Entry<String,Serializable>> lEntrys = oSettings1.GetAll();
		int nCount = 0;
		for (Map.Entry<String,Serializable> oEntry: lEntrys) {
			assertNotNull(oEntry.getValue());
			assertEquals(sValueString2,oEntry.getValue());
			nCount++;
		}
		assertTrue(nCount == 3);

		a_mLogManager.Test("GetString and SetSpecial",0);
		oSettings1.SetSpecial("string1",sValueString1);
		oSettings1.SetSpecial("string2",sValueNoString1);

		assertEquals(sValueString2,oSettings1.Get("string1"));
		assertEquals(sValueNoString1,oSettings1.Get("string2"));

		oSettings1.SetSpecial("integer1",sValueInteger1);
		oSettings1.SetSpecial("integer2",sValueInteger2);
		oSettings1.SetSpecial("integer3",sValueNoInteger1);
		oSettings1.SetSpecial("integer4",sValueNoInteger2);
		oSettings1.SetSpecial("integer5",sValueNoInteger3);
		oSettings1.SetSpecial("integer6",sValueInteger3);

		assertEquals(nInteger1,oSettings1.Get("integer1"));
		assertEquals(nInteger1,oSettings1.Get("integer2"));
		assertEquals(dReal2,oSettings1.Get("integer3"));
		assertEquals(dReal1,oSettings1.Get("integer4"));
		assertEquals(sValueInteger1,oSettings1.Get("integer5"));
		assertEquals(nInteger2,oSettings1.Get("integer6"));

		oSettings1.SetSpecial("real1",sValueReal1);
		oSettings1.SetSpecial("real2",sValueReal2);
		oSettings1.SetSpecial("real3",sValueReal3);
		oSettings1.SetSpecial("real4",sValueReal4);
		oSettings1.SetSpecial("real5",sValueNoReal1);
		oSettings1.SetSpecial("real6",sValueNoReal2);
		oSettings1.SetSpecial("real7",sValueNoReal3);
		oSettings1.SetSpecial("real8",sValueNoReal4);
		oSettings1.SetSpecial("real9",sValueNoReal5);
		oSettings1.SetSpecial("real10",sValueReal5);
		oSettings1.SetSpecial("real11",sValueReal6);

		assertEquals(dReal2,oSettings1.Get("real1"));
		assertEquals(dReal1,oSettings1.Get("real2"));
		assertEquals(dReal3,oSettings1.Get("real3"));
		assertEquals(dReal3,oSettings1.Get("real4"));
		assertEquals(sValueNoReal1,oSettings1.Get("real5"));
		assertEquals(sValueInteger1,oSettings1.Get("real6"));
		assertEquals(sValueReal1,oSettings1.Get("real7"));
		assertEquals(sValueReal2,oSettings1.Get("real8"));
		assertEquals(sValueReal3,oSettings1.Get("real9"));
		assertEquals(dReal5,oSettings1.Get("real10"));
		assertEquals(dReal4,oSettings1.Get("real11"));

		oSettings1.SetSpecial("boolean1",sValueBoolean1);
		oSettings1.SetSpecial("boolean2",sValueBoolean2);
		oSettings1.SetSpecial("boolean3",sValueBoolean3);
		oSettings1.SetSpecial("boolean4",sValueNoBoolean1);
		oSettings1.SetSpecial("boolean5",sValueNoBoolean2);
		oSettings1.SetSpecial("boolean6",sValueNoBoolean3);

		assertEquals(bBoolean1,oSettings1.Get("boolean1"));
		assertEquals(bBoolean2,oSettings1.Get("boolean2"));
		assertEquals(bBoolean2,oSettings1.Get("boolean3"));
		assertEquals(sValueNoBoolean1,oSettings1.Get("boolean4"));
		assertEquals(sValueBoolean1,oSettings1.Get("boolean5"));
		assertEquals(sValueBoolean2,oSettings1.Get("boolean6"));

		oSettings1.SetSpecial("null1",sValueNull);
		oSettings1.SetSpecial("null2",sValueNoNull);

		assertNull(oSettings1.Get("null1"));
		assertEquals(sValueNull,oSettings1.Get("null2"));

		a_mLogManager.Test("end",0);
	}
}
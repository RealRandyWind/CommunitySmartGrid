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
		a_mLogManager.Test("[SettingsTest.testGetIdentifier] begin",0);
		UUID iSettings2 = UUID.randomUUID();
		ISettings oSettings1 = new Settings(null);
		ISettings oSettings2 = new Settings(iSettings2);

		assertNotNull(oSettings1.GetIdentifier());
		assertEquals(iSettings2,oSettings2.GetIdentifier());
		a_mLogManager.Test("[SettingsTest.testGetIdentifier] end",0);
	}

	@Test
	public void testSetSetSpecialGetGetAllGetString() throws Exception {
		a_mLogManager.Test("[SettingsTest.testSetSetSpecialGetGetAllGetString] begin",0);
		String sValue = "serializable";
		String sValue2 = "\"serializable\"";
		String sValue3 ="a\"serializable\"";
		ISettings oSettings1 = new Settings(null);

		a_mLogManager.Test("[SettingsTest.testSetSetSpecialGetGetAllGetString] Set and Get",0);
		assertEquals("null",oSettings1.GetString("testit"));
		oSettings1.Set("testit",sValue);
		assertEquals(sValue,oSettings1.GetString("testit"));

		assertNull(oSettings1.Get("testit2"));
		oSettings1.Set("testit2",sValue);
		assertEquals(sValue,oSettings1.Get("testit2"));

		assertNull(oSettings1.Get("testit3"));
		oSettings1.Set("testit3",sValue);
		assertEquals(sValue,oSettings1.Get("testit3"));

		a_mLogManager.Test("[SettingsTest.testSetSetSpecialGetGetAllGetString] GetAll",0);
		Iterable<Map.Entry<String,Serializable>> lEntrys = oSettings1.GetAll();
		int nCount = 0;
		for (Map.Entry<String,Serializable> oEntry: lEntrys) {
			String sKey = oEntry.getKey();
			assertNotNull(oEntry.getValue());
			assertEquals(sValue,oEntry.getValue());
			nCount++;
		}
		assertTrue(nCount == 3);

		a_mLogManager.Test("[SettingsTest.testSetSetSpecialGetGetAllGetString] GetString and SetSpecial",0);
		oSettings1.SetSpecial("testit4",sValue2);
		oSettings1.SetSpecial("testit5",sValue3);

		assertEquals(sValue,oSettings1.GetString("testit4"));
		assertEquals(sValue3,oSettings1.GetString("testit5"));
		a_mLogManager.Test("[SettingsTest.testSetSetSpecialGetGetAllGetString] end",0);
	}
}
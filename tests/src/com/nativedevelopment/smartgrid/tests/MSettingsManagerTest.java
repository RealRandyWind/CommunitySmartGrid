package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.ISettings;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.MSettingsManager;
import com.nativedevelopment.smartgrid.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MSettingsManagerTest implements ITestCase {
	MLogManager a_mLogManager = null;
	MSettingsManager a_mSettingsManager = null;

	@Before
	public void setUp() throws Exception {
		a_mLogManager = MLogManager.GetInstance();
		a_mSettingsManager = MSettingsManager.GetInstance();

		a_mLogManager.SetUp();
		a_mSettingsManager.SetUp();
	}

	@After
	public void tearDown() throws Exception {
		a_mSettingsManager.ShutDown();
		a_mLogManager.ShutDown();
	}

	@Test
	public void testLoadSettingsFromFileFromString() throws Exception {
		a_mLogManager.Test("begin",0);
		ISettings oSettingsSimple = a_mSettingsManager.LoadSettingsFromFile("tests\\deps\\simple.settings");
		assertEquals(oSettingsSimple.Get("setting1"),"settingA");
		assertEquals(oSettingsSimple.Get("setting2"),"settingB");
		assertEquals(oSettingsSimple.Get("setting 3"),"setting C");
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testAddGetGetAllSettings() throws Exception {
		a_mLogManager.Test("begin",0);
		ISettings oSettings1 = new Settings(null);
		ISettings oSettings2 = new Settings(null);
		ISettings oSettings3 = new Settings(null);

		a_mLogManager.Test("Get and Add",0);
		assertNull(a_mSettingsManager.GetSettings(oSettings1.GetIdentifier()));
		a_mSettingsManager.AddSettings(oSettings1);
		assertSame(oSettings1,a_mSettingsManager.GetSettings(oSettings1.GetIdentifier()));

		assertNull(a_mSettingsManager.GetSettings(oSettings2.GetIdentifier()));
		a_mSettingsManager.AddSettings(oSettings2);
		assertSame(oSettings2,a_mSettingsManager.GetSettings(oSettings2.GetIdentifier()));

		assertNull(a_mSettingsManager.GetSettings(oSettings3.GetIdentifier()));
		a_mSettingsManager.AddSettings(oSettings3);
		assertSame(oSettings3,a_mSettingsManager.GetSettings(oSettings3.GetIdentifier()));

		a_mLogManager.Test("GetAll",0);
		int nCount = 0;
		Iterable<ISettings> lSettings = a_mSettingsManager.GetAllSettings();
		for (ISettings oSettings : lSettings ) {
			assertNotNull(oSettings);
			nCount++;
		}
		assertTrue(nCount==3);
		a_mLogManager.Test("end",0);
	}
}
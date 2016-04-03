package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IPackage;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.Package;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;

public class PackageTest implements ITestCase {

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
	public void testGetCorrelationRouteIdentifierGetFlagGetContent() throws Exception {
		a_mLogManager.Test("[PackageTest.testGetCorrelationRouteIdentifierGetFlagGetContent] begin",0);
		UUID iCorrelationIdentifier = UUID.randomUUID();
		UUID iRouteIdentifier = UUID.randomUUID();
		int nFlag = 2;
		Serializable oContent = new SerializableObject("SerializableObject");
		IPackage oPackage = new Package(oContent, iRouteIdentifier, iCorrelationIdentifier, nFlag);

		assertEquals(iCorrelationIdentifier,oPackage.GetCorrelationIdentifier());
		assertEquals(iRouteIdentifier,oPackage.GetRoutIdentifier());
		assertEquals(nFlag,oPackage.GetFlag());
		assertEquals(oContent,oPackage.GetContent());
		a_mLogManager.Test("[PackageTest.testGetCorrelationRouteIdentifierGetFlagGetContent] end",0);
	}
}
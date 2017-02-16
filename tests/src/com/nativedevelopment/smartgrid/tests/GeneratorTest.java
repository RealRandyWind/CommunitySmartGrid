package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.IData;
import com.nativedevelopment.smartgrid.MLogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class GeneratorTest implements ITestCase {
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
	public void testGenerateDataSensor() throws Exception {
		a_mLogManager.Test("begin",0);
		int nSamples = 5;
		for (int iSample = 0; iSample < nSamples; ++iSample) {
			IData oData = Generator.GenerateDataSensor(UUID.randomUUID(),iSample);
			for (int iTuple = 0; iTuple < iSample; ++iTuple) {
				System.out.printf("_DEBUG: %sdata(%d,%d) %s.\n"
						,MLogManager.MethodName()
						,iSample
						,iTuple
						,Arrays.toString(oData.GetTuple(iTuple)));
			}
		}
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGenerateDataMachine() throws Exception {
		a_mLogManager.Test("begin",0);
		int nSamples = 5;
		for (int iSample = 0; iSample < nSamples; ++iSample) {
			IData oData = Generator.GenerateDataMachine(UUID.randomUUID(),iSample + 1);
			for (int iTuple = 0; iTuple < iSample; ++iTuple) {
				System.out.printf("_DEBUG: %sdata(%d,%d) %s.\n"
						,MLogManager.MethodName()
						,iSample
						,iTuple
						,Arrays.toString(oData.GetTuple(iTuple)));
			}
		}
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGenerateResult() throws Exception {
		a_mLogManager.Test("begin",0);
		UUID[] lActions = new UUID[5];
		for(int iAction = 0; iAction < lActions.length; ++iAction) {
			lActions[iAction] = UUID.randomUUID();
		}
		int nSamples = 5;
		for (int iSample = 0; iSample < nSamples; ++iSample) {
			IData oData = Generator.GenerateResult(UUID.randomUUID(),iSample + 1, lActions);
			for (int iTuple = 0; iTuple < iSample; ++iTuple) {
				System.out.printf("_DEBUG: %sdata(%d,%d) %s.\n"
						,MLogManager.MethodName()
						,iSample
						,iTuple
						,Arrays.toString(oData.GetTuple(iTuple)));
			}
		}
		a_mLogManager.Test("end",0);
	}

	@Test
	public void testGenerateActionSensorMachineServer() throws Exception {
		a_mLogManager.Test("begin",0);
		UUID[] lActions = new UUID[5];
		for(int iAction = 0; iAction < lActions.length; ++iAction) {
			lActions[iAction] = UUID.randomUUID();
		}
		int nSamples = 5;
		for (int iSample = 0; iSample < nSamples; ++iSample) {
			IAction oAction = Generator.GenerateActionMachine(lActions);
			System.out.printf("_DEBUG: %saction(%d) %s%s.\n"
					,MLogManager.MethodName()
					,iSample
					,oAction.GetIdentifier()
					,Arrays.toString(oAction.GetParameters()));
		}
		a_mLogManager.Test("end",0);
	}
}
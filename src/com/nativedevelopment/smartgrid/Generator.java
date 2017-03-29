package com.nativedevelopment.smartgrid;

import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.IData;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

public final class Generator {
	private static final int DEVICE_SENSOR = 0;
	private static final int DEVICE_MACHINE = 1;
	private static final int DEVICETYPE_COUNT = 2;

	private static Random a_oRandom = new Random();

	private Generator() {

	}

	public static void SetSeed(long iSeed) {
		a_oRandom.setSeed(iSeed);
	}

	public static int GenerateDeviceType() {
		return a_oRandom.nextInt(DEVICETYPE_COUNT);
	}

	public static IData GenerateDataDevice(UUID iDevice, int nTuple, int eDevice) {
		if(eDevice == DEVICE_SENSOR) { return GenerateDataSensor(iDevice, nTuple); }
		else if (eDevice == DEVICE_MACHINE) { return GenerateDataMachine(iDevice, nTuple); }
		return null;
	}

	public static IData GenerateDataSensor(UUID iSensor, int nTuples) {
		/* create attributes */
		String[] lAttributes = new String[4];
		lAttributes[0] = "timestamp";
		lAttributes[1] = "interval";
		lAttributes[2] = "signal";
		lAttributes[3] = "flag";

		/* initialize bounds */
		int nIntervalUpper = 500;
		int nIntervalLower = 10;
		double dSignalStrength = 30;
		double dSignalBias = 60 * a_oRandom.nextDouble();
		double dSignalBiasNoise = 10;
		double dSignalFrequency = 0.1;
		double dSignalFrequencyNoise = 0.04;

		/* generate actual data */
		Serializable[][] lTuples = new Serializable[nTuples][4];
		for (int iTuple = 0; iTuple < nTuples; ++iTuple) {
			long tBegin = System.currentTimeMillis();
			Interval(a_oRandom.nextInt(nIntervalUpper - nIntervalLower) + nIntervalLower);
			long tEnd =  System.currentTimeMillis();
			long tInterval = tEnd - tBegin;
			double dOmega = (2 * Math.PI * dSignalFrequency) + (a_oRandom.nextGaussian() * dSignalFrequencyNoise);
			lTuples[iTuple][0] = tBegin;
			lTuples[iTuple][1] = tInterval;
			lTuples[iTuple][2] = (a_oRandom.nextGaussian() * dSignalBiasNoise) + dSignalBias
					+ (dSignalStrength * dOmega * (Math.cos(tEnd * dOmega) - Math.cos(tBegin * dOmega)));
			lTuples[iTuple][3] = a_oRandom.nextLong();
		}
		return new Data(iSensor, lTuples, lAttributes);
	}

	public static IData GenerateDataMachine(UUID iMachine, int nTuples) {
		/* create attributes */
		String[] lAttributes = new String[6];
		lAttributes[0] = "timestamp";
		lAttributes[1] = "interval";
		lAttributes[2] = "activity";
		lAttributes[3] = "handled";
		lAttributes[4] = "unhandled";
		lAttributes[5] = "flag";

		/* initialize bounds */
		int nIntervalUpper = 500;
		int nIntervalLower = 10;
		int nMaxLoad = 80;
		double dSignalStrength = 30;
		double dSignalBias = 30 + (40 * a_oRandom.nextDouble());
		double dSignalBiasNoise = 10;
		double dSignalFrequency = 0.1;
		double dSignalFrequencyNoise = 0.04;

		/* generate actual data */
		Serializable[][] lTuples = new Serializable[nTuples][6];
		for (int iTuple = 0; iTuple < nTuples; ++iTuple) {
			long tBegin = System.currentTimeMillis();
			Interval(a_oRandom.nextInt(nIntervalUpper - nIntervalLower) + nIntervalLower);
			long tEnd =  System.currentTimeMillis();
			long tInterval = tEnd - tBegin;
			double dOmega = (2 * Math.PI * dSignalFrequency) + (a_oRandom.nextGaussian() * dSignalFrequencyNoise);
			lTuples[iTuple][0] = tBegin;
			lTuples[iTuple][1] = tInterval;
			lTuples[iTuple][2] = (a_oRandom.nextGaussian() * dSignalBiasNoise) + dSignalBias
					+ (dSignalStrength * dOmega * (Math.cos(tEnd * dOmega) - Math.cos(tBegin * dOmega)));
			lTuples[iTuple][3] = a_oRandom.nextInt(nMaxLoad) * tInterval;
			lTuples[iTuple][4] = a_oRandom.nextInt(nMaxLoad) * tInterval;
			lTuples[iTuple][5] = a_oRandom.nextLong();
		}
		return new Data(iMachine, lTuples, lAttributes);
	}

	public static IData GenerateResult(UUID iDevice,int nTuples, UUID[] lActions) {
		/* create attributes */
		String[] lAttributes = new String[5];
		lAttributes[0] = "timestamp";
		lAttributes[1] = "interval";
		lAttributes[2] = "action";
		// TODO also add arguments used
		lAttributes[3] = "weight";
		lAttributes[4] = "flag";

		/* initialize bounds */
		int nIntervalUpper = 500;
		int nIntervalLower = 10;

		/* generate actual data */
		Serializable[][] lTuples = new Serializable[nTuples][5];
		for (int iTuple = 0; iTuple < nTuples; ++iTuple) {
			UUID iAction = (lActions == null ? UUID.randomUUID() : lActions[a_oRandom.nextInt(lActions.length)]);
			long tBegin = System.currentTimeMillis();
			Interval(a_oRandom.nextInt(nIntervalUpper - nIntervalLower) + nIntervalLower);
			long tEnd =  System.currentTimeMillis();
			long tInterval = tEnd - tBegin;
			lTuples[iTuple][0] = tBegin;
			lTuples[iTuple][1] = tInterval;
			lTuples[iTuple][2] = iAction;
			// TODO also add arguments used
			lTuples[iTuple][3] = a_oRandom.nextDouble();
			lTuples[iTuple][4] = a_oRandom.nextLong();
		}
		return new Data(iDevice, lTuples, lAttributes);
	}

	public static IAction GenerateActionSensor(UUID[] lActions) {
		UUID iAction = (lActions == null ? UUID.randomUUID() : lActions[a_oRandom.nextInt(lActions.length)]);
		// TODO also add arguments used
		return new Action(iAction, new Serializable[0]);
	}

	public static IAction GenerateActionMachine(UUID[] lActions) {
		UUID iAction = (lActions == null ? UUID.randomUUID() : lActions[a_oRandom.nextInt(lActions.length)]);
		// TODO also add arguments used
		return new Action(iAction, new Serializable[0]);
	}

	public static IAction GenerateActionServer(UUID[] lActions) {
		UUID iAction = (lActions == null ? UUID.randomUUID() : lActions[a_oRandom.nextInt(lActions.length)]);
		// TODO also add arguments used
		return new Action(iAction, new Serializable[0]);
	}

	public static void Interval(long nLength) {
		try {
			Thread.sleep(nLength);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

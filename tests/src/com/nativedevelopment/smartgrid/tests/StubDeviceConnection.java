package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.IData;
import com.nativedevelopment.smartgrid.connection.CommDeviceConnection;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

public class StubDeviceConnection extends CommDeviceConnection{
	private static final int RANDOM_INTERVAL_UPPERBOUND = 10000;
	private static final int RANDOM_INTERVAL_LOWERBOUND = 100;
	private static final float RANDOM_ACTIVITY_UPPERBOUND = 10000.0f;
	private static final float RANDOM_ACTIVITY_LOWERBOUND = 10.0f;
	private static final float RANDOM_ACTIVITY_SIGNBIAS = 0.7f;
	private static final Random a_oRandom = new Random();

	private int a_nIntervalBound = a_oRandom.nextInt(RANDOM_INTERVAL_UPPERBOUND - RANDOM_INTERVAL_LOWERBOUND) + RANDOM_INTERVAL_LOWERBOUND;
	private float a_dActivityBound = a_oRandom.nextFloat() * (RANDOM_ACTIVITY_UPPERBOUND - RANDOM_ACTIVITY_LOWERBOUND) + RANDOM_ACTIVITY_LOWERBOUND;
	private float a_dActivitySign = a_oRandom.nextFloat() >= RANDOM_ACTIVITY_SIGNBIAS ? -1.0f : 1.0f;
	private long a_tPreviousTimeMillis = 0;

	private Queue<Serializable> a_lQueue = null;

	public StubDeviceConnection(UUID oIdentifier, UUID iDevice, String[] lAttributes,
								AbstractMap<UUID, Serializable> lActionMap,
								Queue<Serializable> a_lToQueue, Queue<IAction> a_lFromQueue,
								Queue<Serializable> lToLogQueue, Queue<Serializable> lQueue) {
		super(oIdentifier, iDevice, lAttributes, lActionMap, a_lToQueue, a_lFromQueue, lToLogQueue);
		a_lQueue = lQueue;
	}

	private IData Fx_ProduceData() throws Exception {
		Thread.sleep(a_oRandom.nextInt(a_nIntervalBound));
		Serializable[][] lTuples = new Serializable[1][a_lAttributes.length];

		long tCurrentTimeMillis = System.currentTimeMillis();

		lTuples[0][0] = tCurrentTimeMillis;
		lTuples[0][1] = tCurrentTimeMillis - a_tPreviousTimeMillis;
		lTuples[0][2] = a_oRandom.nextFloat() * a_dActivityBound * a_dActivitySign;

		a_tPreviousTimeMillis = tCurrentTimeMillis;
		return new Data(a_iDevice, lTuples, a_lAttributes);
	}

	private void Fx_PerformAction(IAction oAction) {
		a_lQueue.offer(oAction.GetIdentifier());
		if (a_lActionMap.containsKey(oAction.GetIdentifier())) {
			System.out.printf("_DEBUG: [StubDeviceConnection.Fx_PerformAction] action %s \"%s\"\n",oAction.GetIdentifier(),a_lActionMap.get(oAction.GetIdentifier()));
		} else {
			System.out.printf("_DEBUG: [StubDeviceConnection.Fx_PerformAction] action %s not available.\n",oAction.GetIdentifier());
		}
	}

	@Override
	public void Run() {
		a_tPreviousTimeMillis = System.currentTimeMillis();
		try {
			while (!IsClose()) {
				a_lToQueue.offer(Fx_ProduceData());
				Fx_PerformAction(a_lFromQueue.poll());
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: [StubDeviceConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

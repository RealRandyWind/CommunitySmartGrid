package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.IData;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.connection.CommDeviceConnection;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

public class StubDeviceConnection extends CommDeviceConnection{
	private static final int RANDOM_INTERVAL_UPPERBOUND = 5000;
	private static final int RANDOM_INTERVAL_LOWERBOUND = 500;
	private static final float RANDOM_ACTIVITY_UPPERBOUND = 5000.0f;
	private static final float RANDOM_ACTIVITY_LOWERBOUND = 50.0f;
	private static final float RANDOM_ACTIVITY_SIGNBIAS = 0.7f;
	private static final int CHECK_INTERVAL = 50;
	private static final Random a_oRandom = new Random();

	private int a_nIntervalBound = a_oRandom.nextInt(RANDOM_INTERVAL_UPPERBOUND - RANDOM_INTERVAL_LOWERBOUND) + RANDOM_INTERVAL_LOWERBOUND;
	private float a_dActivityBound = a_oRandom.nextFloat() * (RANDOM_ACTIVITY_UPPERBOUND - RANDOM_ACTIVITY_LOWERBOUND) + RANDOM_ACTIVITY_LOWERBOUND;
	private float a_dActivitySign = a_oRandom.nextFloat() >= RANDOM_ACTIVITY_SIGNBIAS ? -1.0f : 1.0f;
	private long a_tPreviousTimeMillis = 0;
	private long a_tCurrentTimeMillis = 0;
	private long a_tIntervalTimeMillis = 0;

	private Queue<Serializable> a_lQueue = null;

	public StubDeviceConnection(UUID oIdentifier, UUID iDevice, String[] lAttributes,
								AbstractMap<UUID, Serializable> lActionMap, Queue<Serializable> lQueue) {
		super(oIdentifier, iDevice, lAttributes, lActionMap);
		a_lQueue = lQueue;
		System.out.printf("_INFO: %sIntervalBound %d, ActivityBound %f, ActivitySign %f.\n"
				,MLogManager.MethodName()
				,a_nIntervalBound,a_dActivityBound,a_dActivitySign);
	}

	private IData Fx_ProduceData() {
		a_tIntervalTimeMillis = a_oRandom.nextInt(a_nIntervalBound);
		Serializable[][] lTuples = new Serializable[1][a_lAttributes.length];

		lTuples[0][0] = a_tCurrentTimeMillis;
		lTuples[0][1] = a_tCurrentTimeMillis - a_tPreviousTimeMillis;
		lTuples[0][2] = a_oRandom.nextFloat() * a_dActivityBound * a_dActivitySign;

		System.out.printf("_DEBUG: %sdata (%d, %d, %f).\n"
				,MLogManager.MethodName()
				,lTuples[0][0],lTuples[0][1],lTuples[0][2]);
		return new Data(a_iDevice, lTuples, a_lAttributes);
	}

	private void Fx_PerformAction(IAction oAction) {
		if (a_lActionMap.containsKey(oAction.GetIdentifier())) {
			a_lQueue.offer(oAction.GetIdentifier());
			System.out.printf("_DEBUG: %saction %s \"%s\"\n"
					,MLogManager.MethodName()
					,oAction.GetIdentifier(),a_lActionMap.get(oAction.GetIdentifier()));
		} else {
			System.out.printf("_DEBUG: %saction %s not available.\n"
					,MLogManager.MethodName()
					,oAction.GetIdentifier());
		}
	}

	@Override
	public void Run() {
		a_tCurrentTimeMillis = System.currentTimeMillis();
		a_tPreviousTimeMillis = a_tCurrentTimeMillis;
		try {
			while (!IsClose()) {
				a_tCurrentTimeMillis = System.currentTimeMillis();
				if(a_tCurrentTimeMillis - a_tPreviousTimeMillis >= a_tIntervalTimeMillis) {
					a_lToQueue.offer(Fx_ProduceData());
					a_tPreviousTimeMillis = a_tCurrentTimeMillis;
				}
				if(!a_lFromQueue.isEmpty()) {
					Thread.sleep(a_tIntervalTimeMillis < CHECK_INTERVAL ? a_tCurrentTimeMillis : CHECK_INTERVAL);
					Fx_PerformAction((IAction)a_lFromQueue.poll());
				}
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s%s \"%s\"\n"
					,MLogManager.MethodName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

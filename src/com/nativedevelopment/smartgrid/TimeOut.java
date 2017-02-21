package com.nativedevelopment.smartgrid;

/**
 * Created by Gebruiker on 21/02/2017.
 */
public class TimeOut {
	protected int a_tTime = 0;
	protected int a_tLowerBound = 0;
	protected int a_tUpperBound = 0;
	protected int a_tDeltaCheckTime = 0;

 	public TimeOut() {
	}

	public void Now() throws Exception {
		Thread.sleep(a_tTime);
	}

	public boolean Routine(boolean bCondition) throws Exception {
		if (!bCondition) {
			a_tTime = a_tLowerBound;
			return false;
		}
		Now();
		a_tTime += a_tDeltaCheckTime;
		a_tTime = a_tTime >= a_tUpperBound ? a_tUpperBound : a_tTime;
		return true;
	}

	public void SetUpperBound(int tUpperBound) {
		a_tUpperBound = tUpperBound;
	}

	public void SetLowerBound(int tLowerBound) {
		a_tLowerBound = tLowerBound;
		a_tTime = (a_tTime < tLowerBound ? tLowerBound : a_tTime);
	}

	public void SetDelta(int tDelta) {
		a_tDeltaCheckTime = tDelta;
	}
}

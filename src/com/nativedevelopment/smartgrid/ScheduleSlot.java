package com.nativedevelopment.smartgrid;

public class ScheduleSlot implements IScheduleSlot{
	private long a_tFrom = 0;
	private long a_tTo = 0;
	private int a_nType = 0;

	public ScheduleSlot(long tFrom, long tTo, int nType) {
		a_tFrom = tFrom;
		a_tTo = tTo;
		a_nType = nType;
	}

	@Override
	public long GetFrom() {
		return a_tFrom;
	}

	@Override
	public long GetTo() {
		return a_tTo;
	}

	@Override
	public long GetLength() {
		return a_tTo - a_tFrom;
	}

	@Override
	public int GetType() {
		return a_nType;
	}
}

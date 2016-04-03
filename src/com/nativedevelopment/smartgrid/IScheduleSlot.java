package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public interface IScheduleSlot extends Serializable {
	public long GetFrom();
	public long GetTo();
	public long GetLength();
	public int GetType();
}

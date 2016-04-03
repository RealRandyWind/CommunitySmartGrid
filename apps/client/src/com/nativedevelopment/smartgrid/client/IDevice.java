package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.IControlable;
import com.nativedevelopment.smartgrid.ScheduleSlot;

import java.util.UUID;

public interface IDevice extends IControlable {
	public UUID GetIdentifier();
	public String[] GetDataSpace();
}
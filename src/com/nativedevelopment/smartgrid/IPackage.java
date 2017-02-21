package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IPackage extends Serializable{
	public UUID GetRoutIdentifier();
	public UUID GetCorrelationIdentifier();
	public int GetFlag();
	public long GetTimeStamp();
	// TODO maybe change return Serializable to bytes[]
	public Serializable GetContent();
}

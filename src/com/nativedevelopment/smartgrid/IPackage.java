package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IPackage extends Serializable{
	public UUID GetRoutIdentifier();
	public UUID GetCorrelationIdentifier();
	public int GetFlag();
	public Serializable GetContent();
}

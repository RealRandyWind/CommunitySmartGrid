package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IAddress extends Serializable {
	public UUID GetConnectionIdentifier();
	public UUID GetContext();
	public String GetHost();
	public int GetPort();
}

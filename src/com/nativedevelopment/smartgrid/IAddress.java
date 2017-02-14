package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IAddress extends Serializable {
	public UUID GetIdentifier();
	public UUID GetContext();
	public String GetHost();
	public int GetPort();
}

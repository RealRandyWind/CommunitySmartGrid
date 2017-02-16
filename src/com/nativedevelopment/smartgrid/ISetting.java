package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public interface ISetting extends Serializable{
	public String GetKey();
	public Serializable GetValue();
}

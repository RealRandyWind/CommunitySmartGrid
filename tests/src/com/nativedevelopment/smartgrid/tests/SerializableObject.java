package com.nativedevelopment.smartgrid.tests;

import java.io.Serializable;

public class SerializableObject implements Serializable {
	String a_sName = null;
	public SerializableObject(String sName) {
		a_sName = sName;
	}

	String GetName() {
		return a_sName;
	}
}


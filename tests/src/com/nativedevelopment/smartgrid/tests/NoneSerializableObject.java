package com.nativedevelopment.smartgrid.tests;

public class NoneSerializableObject {
	String a_sName = null;
	public NoneSerializableObject(String sName) {
		a_sName = sName;
	}

	String GetName() {
		return a_sName;
	}
}

package com.nativedevelopment.smartgrid.tests;

public class NoneSerializable {
	String a_sName = null;
	public NoneSerializable(String sName) {
		a_sName = sName;
	}

	String GetName() {
		return a_sName;
	}
}

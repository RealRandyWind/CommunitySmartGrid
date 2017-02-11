package com.nativedevelopment.smartgrid.tests;

public class NoneSerializableObject {
	String a_sName = null;
	public NoneSerializableObject(String sName) {
		a_sName = sName;
	}

	String GetName() {
		return a_sName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NoneSerializableObject)) {
			return false;
		}
		NoneSerializableObject oNoneSerializableObject = (NoneSerializableObject) obj;
		return a_sName.equals(oNoneSerializableObject.a_sName);
	}
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public class Promise implements IPromise {
	private Serializable a_oSerializable = null;
	private boolean a_bIsChanged = false;

	public Promise() {

	}

	@Override
	public boolean IsDone() {
		return a_oSerializable != null;
	}

	@Override
	public boolean IsChanged() {
		return a_bIsChanged;
	}

	@Override
	public Serializable Get() {
		a_bIsChanged = false;
		return a_oSerializable;
	}

	@Override
	public void Set(Serializable oSerializable) {
		a_bIsChanged = true;
		a_oSerializable = oSerializable;
	}
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public interface IPromise extends Serializable {
	// TODO add Cancel, and IsCanceled
	public boolean IsDone();
	public boolean IsChanged();
	public Serializable Get();
	public void Set(Serializable oSerializable);
}

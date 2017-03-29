package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public class Converter implements IConverter {
	@Override
	public Serializable Do(Serializable ptrFrom, int iIndex) {
		return ptrFrom;
	}

	@Override
	public Serializable Undo(Serializable ptrFrom, int iIndex) {
		return ptrFrom;
	}
}

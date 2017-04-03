package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public interface IConverter extends Serializable{
	public Serializable Do(Serializable ptrFrom, int iIndex);
	public Serializable Undo(Serializable ptrFrom, int iIndex);
	public Serializable ExceptionsDo(Serializable ptrValue);
	public Serializable ExceptionsUndo(Serializable ptrValue);
}
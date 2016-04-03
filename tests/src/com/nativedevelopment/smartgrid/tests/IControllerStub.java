package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IController;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;

public interface IControllerStub extends IController {
	public void ProcedureNoArguments() throws RemoteException;
	public void ProcedureArgumentsSerializable(Serializable oAnyArgument, Serializable oAnOther) throws RemoteException;
	public Serializable FunctionReturnNoArgumentsSerializable() throws RemoteException;
	public Serializable FunctionReturnArgumentsSerializable(Serializable oAnyArgument, Serializable oAnOther) throws RemoteException;
	public Iterable<Serializable> FunctionReturnListNoArgumentsSerializable() throws RemoteException;
	public Map<Serializable,Serializable> FunctionReturnMapNoArgumentsSerializable() throws RemoteException;
}
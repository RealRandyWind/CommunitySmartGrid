package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StubController implements IStubController {
	public enum EMethods {
		PROCEDURENOARGUMENTS,
		PROCEDUREARGUMENTSSERIALIZABLE,
		FUNCTIONRETURNNOARGUMENTSSERIALIZABLE,
		FUNCTIONRETURNARGUMENTSSERIALIZABLE,
		FUNCTIONRETURNLISTNOARGUMENTSSERIALIZABLE,
		FUNCTIONRETURNMAPNOARGUMENTSSERIALIZABLE
	}

	private Queue<Serializable> a_lQueue = null;
	private Map<Serializable,Serializable> a_lSerializables = null;
	private Serializable a_oSerializable = null;

	public StubController(Queue<Serializable> lQueue, Serializable oSerializable, Map<Serializable,Serializable> lSerializables) {
		a_lQueue = lQueue;
		a_lSerializables = lSerializables;
		a_oSerializable = oSerializable;
	}

	@Override
	public void ProcedureNoArguments() throws RemoteException {
		a_lQueue.offer(EMethods.PROCEDURENOARGUMENTS);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
	}

	@Override
	public void ProcedureArgumentsSerializable(Serializable oAnyArgument, Serializable oAnOther) throws RemoteException {
		a_lQueue.offer(EMethods.PROCEDUREARGUMENTSSERIALIZABLE);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
	}

	@Override
	public Serializable FunctionReturnNoArgumentsSerializable() throws RemoteException {
		a_lQueue.offer(EMethods.FUNCTIONRETURNNOARGUMENTSSERIALIZABLE);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
		return a_oSerializable;
	}

	@Override
	public Serializable FunctionReturnArgumentsSerializable(Serializable oAnyArgument, Serializable oAnOther) throws RemoteException {
		a_lQueue.offer(EMethods.FUNCTIONRETURNARGUMENTSSERIALIZABLE);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
		return a_oSerializable;
	}

	@Override
	public Iterable<Serializable> FunctionReturnListNoArgumentsSerializable() throws RemoteException {
		a_lQueue.offer(EMethods.FUNCTIONRETURNLISTNOARGUMENTSSERIALIZABLE);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
		return new LinkedList<>(a_lSerializables.values());
	}

	@Override
	public Map<Serializable,Serializable> FunctionReturnMapNoArgumentsSerializable() throws RemoteException {
		a_lQueue.offer(EMethods.FUNCTIONRETURNMAPNOARGUMENTSSERIALIZABLE);
		System.out.printf("_DEBUG: %scalled.\n", MLogManager.MethodName());
		return a_lSerializables;
	}
}

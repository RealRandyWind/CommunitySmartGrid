package com.nativedevelopment.smartgrid;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAnalyticServer extends Remote {
	public void ReceiveData(Data data) throws RemoteException;
}

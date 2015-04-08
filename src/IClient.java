package com.nativedevelopment.smartgrid;

import com.nativedevelopment.smartgrid.Action;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface IClient extends Remote {
    public void passActionToDevice(Action action) throws RemoteException;
	public UUID getIdentifier() throws RemoteException;
}
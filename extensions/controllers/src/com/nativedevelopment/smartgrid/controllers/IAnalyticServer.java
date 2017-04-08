package com.nativedevelopment.smartgrid.controllers;

import com.nativedevelopment.smartgrid.IController;

import java.rmi.RemoteException;

public interface IAnalyticServer extends IController {
	public String Notify(String sMessage) throws RemoteException;
}

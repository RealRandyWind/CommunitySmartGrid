package com.nativedevelopment.smartgrid.controllers.interfaces;

import com.nativedevelopment.smartgrid.IController;

import java.rmi.RemoteException;

public interface IAnalyticServerController extends IController {
	public String Notify(String sMessage) throws RemoteException;
}

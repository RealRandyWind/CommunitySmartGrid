package com.nativedevelopment.smartgrid.server.analytic.controllers;

import com.nativedevelopment.smartgrid.Controller;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.controllers.interfaces.IAnalyticServerController;
import com.nativedevelopment.smartgrid.server.analytic.AnalyticServer;

import java.rmi.RemoteException;

public class AnalyticServerController extends Controller implements IAnalyticServerController{
    protected MLogManager a_mLogManager = MLogManager.GetInstance();
    private AnalyticServer a_oApplication = (AnalyticServer)AnalyticServer.GetInstance();

    public static Controller GetInstance() {
        if(a_oInstance != null) { return a_oInstance; }
        a_oInstance = new AnalyticServerController();
        return a_oInstance;
    }

    @Override
    public String Notify(String sMessage) throws RemoteException {
        a_mLogManager.Log("call from \"%s\"",0,sMessage);
        return String.format("reply to \"%s\"",sMessage);
    }
}

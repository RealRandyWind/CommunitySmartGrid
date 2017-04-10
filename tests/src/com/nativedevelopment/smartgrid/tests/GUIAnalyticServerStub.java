package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.*;
import com.nativedevelopment.smartgrid.connections.RMIControllerCallerConnection;
import com.nativedevelopment.smartgrid.controllers.interfaces.IAnalyticServerController;

import java.util.UUID;

public class GUIAnalyticServerStub extends AServerStub {
	private RMIControllerCallerConnection a_oControllerCaller = null;

	private IPromise a_oPromise = null;
	private IAnalyticServerController a_oControllerStub = null;

	private String a_sExchange = null;


	public GUIAnalyticServerStub(UUID oIdentifier, String sRemote, String sExchange, int iPortRMI) {
		super(oIdentifier);
		a_oControllerCaller = new RMIControllerCallerConnection(null);
		ISettings oControllerCallerSettings = NewControllerCallerSettings(sRemote,sExchange,iPortRMI,null);
		a_oControllerCaller.Configure(oControllerCallerSettings);
		a_oPromise = new Promise();
		a_oControllerCaller.SetPromise(a_oPromise);
		a_oTimeOut.SetLowerBound(200);
		a_sExchange = sExchange;
	}

	public void Start() {
		a_mLogManager.Info("controller.caller (RMI) \"%s\"",0,a_oControllerCaller.GetIdentifier().toString());
		a_oControllerCaller.Open();
		a_bIsStop = false;
		a_oThread.start();
	}

	public void Stop() throws InterruptedException {
		a_bIsStop = true;
		a_oControllerCaller.Close();
		a_oThread.join();
	}

	@Override
	public void run() {
		a_mLogManager.Debug("%s running",0, GetIdentifier().toString());
		try {
			while(!a_bIsStop) {
				a_oTimeOut.Now();
				if(!a_oPromise.IsDone()) { continue; }
				a_oControllerStub = (IAnalyticServerController) a_oPromise.Get();
				a_mLogManager.Log("\"%s\" %s", 0
						,a_sExchange
						,a_oControllerStub.Notify(GetIdentifier().toString()));
			}
		} catch (Exception oException) {
			a_mLogManager.Warning("%s \"%s\"\n",0
					,a_oThread.getName()
					,oException.getClass().getCanonicalName(),oException.getMessage());
			oException.printStackTrace();
		}
		a_mLogManager.Debug("%s stopped",0, GetIdentifier().toString());
	}
}

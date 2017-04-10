package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.Controller;
import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.controllers.interfaces.IAnalyticServerController;

public class ControllerServerAnalyticStubController extends Controller implements IAnalyticServerController {
	protected MLogManager a_mLogManager = MLogManager.GetInstance();

	private ControllerServerAnalyticStubController() {
	}

	public static Controller GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new ControllerServerAnalyticStubController();
		return a_oInstance;
	}

	@Override
	public String Notify(String sMessage) {
		a_mLogManager.Log("call from \"%s\"",0,sMessage);
		return String.format("reply to \"%s\"",sMessage);
	}
}

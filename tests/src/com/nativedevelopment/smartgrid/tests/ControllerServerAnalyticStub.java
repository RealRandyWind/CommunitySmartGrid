package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.controller.IAnalyticServer;

public class ControllerServerAnalyticStub implements IAnalyticServer{
	protected MLogManager a_mLogManager = MLogManager.GetInstance();

	public ControllerServerAnalyticStub() {
	}

	@Override
	public String Notify(String sMessage) {
		a_mLogManager.Log("call from \"%s\"",0,sMessage);
		return String.format("reply to \"%s\"",sMessage);
	}
}

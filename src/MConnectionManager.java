package com.nativedevelopment.smartgrid;

import java.util.Vector;
import java.util.List;

public class MConnectionManager
{
	private static MConnectionManager a_oInstance = null;
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = true;

	private Vector<Connection> a_lProducts = null;

	private MLogManager logManager = MLogManager.GetInstance();

	private MConnectionManager() {
		a_bIsShutDown = false;
		a_bIsSetUp = false;
	} 

	public static MConnectionManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MConnectionManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			return; 
		}
		a_bIsShutDown = false;

		// TODO MConnectionManager SetUp

		a_bIsSetUp = true;
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			return; 
		}
		a_bIsSetUp = false;

		// TODO MConnectionManager ShutDown

		a_bIsShutDown = true;
	}
}

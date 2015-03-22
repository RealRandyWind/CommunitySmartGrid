package com.nativedevelopment.smartgrid;

import java.util.Vector;
import java.net.Socket;
import java.net.ServerSocket;

public class MConnectionManager {
	private static MConnectionManager a_oInstance = null;
	
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = false;
	private boolean a_bIsRunning = false;
	private boolean a_bIsStopping = false;

	private ServerSocket a_oServerSocket = null;

	private Vector<Connection> a_lConnections = null;

	private MLogManager mLogManager = MLogManager.GetInstance();

	private MConnectionManager() {

	}

	public static MConnectionManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new MConnectionManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			mLogManager.Warning("[MConnectionManager.SetUp] already setup",0);
			return; 
		}

		a_bIsShutDown = false;

		try {
			a_oServerSocket = new ServerSocket(4444); //TODO fix magic
		} catch (Exception e) {
			mLogManager.Error(e.getMessage(),0);
		}
		// TODO MConnectionManager SetUp
		
		a_bIsSetUp = true;
		mLogManager.Success("[MConnectionManager.SetUp]",0);
	}

	public void Run() {
		if(a_bIsShutDown || a_bIsRunning) {
			mLogManager.Warning("[MConnectionManager.ShutDown] allready runnning or is not setup yet",0);
			return; 
		}

		while(!a_bIsStopping) {
			try {
				Socket socket = a_oServerSocket.accept();
				Fx_AddConnection(new Connection(socket,null));
				mLogManager.Log("[MConnectionManager.Run] new connection s%",0,socket);
			} catch (Exception e) {
				mLogManager.Error(e.getMessage(),0);
			}
		}
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			mLogManager.Warning("[MConnectionManager.ShutDown]",0);
			return; 
		}
		a_bIsSetUp = false;

		Fx_Stop();
		// TODO MConnectionManager ShutDown

		a_bIsShutDown = true;
		mLogManager.Success("[MConnectionManager.ShutDown]",0);
	}

	public void Fx_Stop() {
		if(a_bIsStopping) {
			mLogManager.Warning("[MConnectionManager.Fx_Stop] stopping allready",0);
			return;
		}

		a_bIsStopping = true;

		try {
			a_oServerSocket.close();
		} catch (Exception e) {
			mLogManager.Error(e.getMessage(),0);
		}	
	}

	private void Fx_AddConnection(Connection connection) {
		a_lConnections.add(connection);
	}
}

package com.nativedevelopment.smartgrid;

import java.util.Vector;
import java.util.List;
import java.net.Socket;
import java.net.ServerSocket;

public class MConnectionManager implements Runnable
{
	private static MConnectionManager a_oInstance = null;
	
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = false;
	private boolean a_bIsRunning = false;
	private boolean a_bIsStopping = false;

	private Thread a_oTread = null;
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
			return; 
		}
		a_bIsShutDown = false;
		
		a_oTread = new Thread(this);
		try {
			a_oServerSocket = new ServerSocket(4444); //TODO fix magic
		} catch (Exception e) {
			mLogManager.Error("[MConnectionManager.Fx_Stop] s%",0,e.getMessage());
		}
		// TODO MConnectionManager SetUp
		a_oTread.start();

		a_bIsSetUp = true;
	}

	protected void Run() throws Exception {
		while(!a_bIsStopping) {
			Socket socket = a_oServerSocket.accept();
			Fx_AddConnection(new Connection(socket,null));
		}
	}

	public void Fx_Stop() {
		mLogManager.Log("[MConnectionManager.Fx_Stop] stopping",0);

		a_bIsStopping = true;

		try {
			a_oServerSocket.close();
		} catch (Exception e) {
			mLogManager.Error("[MConnectionManager.Fx_Stop] s%",0,e.getMessage());
		}	
	}

	private void Fx_AddConnection(Connection connection) {
		a_lConnections.add(connection);
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			return; 
		}
		a_bIsSetUp = false;

		Fx_Stop();
		// TODO MConnectionManager ShutDown

		a_bIsShutDown = true;
	}

	public void run() {
		if(a_bIsStopping) {
			mLogManager.Warning("[MConnectionManager.Run] stopping but run is called",0);
			return;
		}
		if(a_bIsRunning) {
			mLogManager.Warning("[MConnectionManager.Run] already running",0);
			return;
		}
		
		try {
			Run();
		} catch (Exception e) {
			mLogManager.Error("[MConnectionManager.Run] s%",0,e.getMessage());
		}

		a_bIsStopping = false;
		a_bIsRunning = false;

		mLogManager.Success("[MConnectionManager.Run] stopped running",0);
	}
}

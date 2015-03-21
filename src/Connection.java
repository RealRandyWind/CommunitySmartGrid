package com.nativedevelopment.smartgrid;

public class Connection implements Runnable{
	private Socket a_oSocket = null;
	private Thread a_oThread = null;
	
	public Connection(Socket socket) {
		a_oSocket = socket;
		a_oThread = new Thread(this);
		a_oThread.start();
	}

	public void run() {

	}
}

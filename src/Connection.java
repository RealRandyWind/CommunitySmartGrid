package com.nativedevelopment.smartgrid;

import java.net.Socket;

public class Connection implements Runnable {
	private Socket a_oSocket = null;
	private Thread a_oThread = null;
	private ServiceIO a_oServiceIO = null;
	
	public Connection(Socket oSocket, ServiceIO oServiceIO) {
		a_oSocket = oSocket;
		a_oServiceIO = oServiceIO;
		a_oThread = new Thread(this);
		
		a_oThread.start();
	}

	public void run() {
		System.out.printf("_WARNING: [Connection] not yet implemented\n");
	}
}

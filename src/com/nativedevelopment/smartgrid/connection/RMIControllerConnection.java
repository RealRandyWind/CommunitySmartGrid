package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.ISettings;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;

public class RMIControllerConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_EXCHANGE = "exchange";
	public static final String SETTING_KEY_ISREBIND = "isrebind";

	public static final String SETTINGS_KEY_CHECKTIME = "checktime";

	private Queue<Serializable> a_lToLogQueue = null;
	private Remote a_oRemote = null;

	private String a_sExchange = null;
	private String a_sRemoteAddress = null;
	private int a_nRemotePort = 0;
	private int a_nCheckTime = 0;
	private boolean a_bIsRebind = false;

	public RMIControllerConnection(UUID oIdentifier, Queue<Serializable> lToLogQueue) {
		super(oIdentifier);
		a_lToLogQueue = lToLogQueue;
	}

	private boolean Fx_Contains(String sBind, String[] lBinds) {
		// TODO do not know if proper way to check this
		return Arrays.asList(lBinds).contains(sBind);
	}

	public Remote GetInstance() {
		// TODO no portable threading/multiprocessing nor rebinding support here
		return a_oRemote;
	}

	public void Configure(ISettings oConfigurations) {
		a_sExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sRemoteAddress = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_nRemotePort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_nCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIME);
		a_bIsRebind = (boolean)oConfigurations.Get(SETTING_KEY_ISREBIND);
	}

	public void Run() {
		try {
			Registry oRegistry = LocateRegistry.getRegistry(a_sRemoteAddress, a_nRemotePort);

			a_oRemote = oRegistry.lookup(a_sExchange);

			while (!IsClose()) {
				Thread.sleep(a_nCheckTime);
				if(a_bIsRebind) {
					a_oRemote = oRegistry.lookup(a_sExchange);
				}
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: [RMIControllerListenerConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

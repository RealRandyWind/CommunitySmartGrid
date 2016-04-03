package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.IPromise;
import com.nativedevelopment.smartgrid.ISettings;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;

public class RMIControllerCallerConnection extends Connection {
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTINGS_KEY_EXCHANGE = "exchange";
	public static final String SETTING_KEY_ISREBIND = "isrebind";

	public static final String SETTINGS_KEY_CHECKTIME = "checktime";

	private IController a_oRemote = null;
	private IPromise a_oPromise = null;

	private String a_sExchange = null;
	private String a_sRemoteAddress = null;
	private int a_nCheckTime = 0;
	private boolean a_bIsRebind = false;

	public RMIControllerCallerConnection(UUID oIdentifier, Queue<Serializable> lToLogQueue, IPromise oPromise) {
		super(oIdentifier, lToLogQueue);
		// TODO replace oPromise by a Queue of Promises, create Fx_UpdatePromises function.
		a_oPromise = oPromise;
	}

	private boolean Fx_Contains(String sBind, String[] lBinds) {
		// TODO do not know if proper way to check this
		return Arrays.asList(lBinds).contains(sBind);
	}

	public Remote GetInstance() {
		// TODO no portable threading/multiprocessing nor rebinding support here
		return a_oRemote;
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_sRemoteAddress = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_nCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIME);
		a_bIsRebind = (boolean)oConfigurations.Get(SETTING_KEY_ISREBIND);
	}

	@Override
	public void Run() {
		try {
			Registry oRegistry = LocateRegistry.getRegistry(a_sRemoteAddress);

			if(Fx_Contains(a_sExchange, oRegistry.list())) {
				a_oRemote = (IController)oRegistry.lookup(a_sExchange);
				a_oPromise.Set(a_oRemote);
			}

			while (!IsClose()) {
				Thread.sleep(a_nCheckTime);
				if((a_oRemote == null || a_bIsRebind) && Fx_Contains(a_sExchange, oRegistry.list())) {
					a_oRemote = (IController)oRegistry.lookup(a_sExchange);
					a_oPromise.Set(a_oRemote);
				}
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: [RMIControllerCallerConnection.Run] %s \"%s\"\n"
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

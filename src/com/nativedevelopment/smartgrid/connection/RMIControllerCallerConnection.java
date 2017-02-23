package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;

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

	private String a_sExchange = null;
	private String a_sRemoteAddress = null;
	private boolean a_bIsRebind = false;

	protected TimeOut a_oTimeOut = null;
	private IController a_oRemote = null;
	private IPromise a_oPromise = null;

	public RMIControllerCallerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	public void SetPromise(IPromise oPromise) {
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
		a_bIsRebind = (boolean)oConfigurations.Get(SETTING_KEY_ISREBIND);

		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIME));
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
				a_oTimeOut.Now();
				if((a_oRemote == null || a_bIsRebind) && Fx_Contains(a_sExchange, oRegistry.list())) {
					a_oRemote = (IController)oRegistry.lookup(a_sExchange);
					a_oPromise.Set(a_oRemote);
				}
			}
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

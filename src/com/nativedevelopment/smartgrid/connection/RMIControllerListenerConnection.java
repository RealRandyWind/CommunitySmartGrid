package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.*;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.UUID;

public class RMIControllerListenerConnection extends Connection{
	public static final String SETTINGS_KEY_EXCHANGE = "exchange";
	public static final String SETTINGS_KEY_REMOTEPORT = "remote.port";
	public static final String SETTINGS_KEY_REMOTEADDRESS = "remote.address";
	public static final String SETTING_KEY_ISREBIND = "isrebind";
	public static final String SETTING_KEY_ISFORCEUNEXPORT = "isforceunexport";

	public static final String SETTINGS_KEY_CHECKTIMELOWERBOUND = "checktime.lowerbound";

	private String a_sExchange = null;
	private String a_sRemoteAddress = null;
	private int a_iRemotePort = 0;
	private boolean a_bIsRebind = false;
	private boolean a_bIsForceUnExport = false;

	protected TimeOut a_oTimeOut = null;
	volatile private static Registry a_oRegistry = null;
	private IController a_oRemote = null;

	public RMIControllerListenerConnection(UUID oIdentifier) {
		super(oIdentifier);
		a_oTimeOut = new TimeOut();
	}

	public void SetRemote(IController oRemote) {
		a_oRemote = oRemote;
	}

	private boolean Fx_Contains(String sBind, String[] lBinds) {
		// TODO do not know if proper way to check this
		return Arrays.asList(lBinds).contains(sBind);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_iRemotePort = (int)oConfigurations.Get(SETTINGS_KEY_REMOTEPORT);
		a_sRemoteAddress = oConfigurations.GetString(SETTINGS_KEY_REMOTEADDRESS);
		a_bIsRebind = (boolean)oConfigurations.Get(SETTING_KEY_ISREBIND);
		a_bIsForceUnExport = (boolean)oConfigurations.Get(SETTING_KEY_ISFORCEUNEXPORT);

		a_oTimeOut.SetLowerBound((int)oConfigurations.Get(SETTINGS_KEY_CHECKTIMELOWERBOUND));
	}

	@Override
	public void Run() {
		try {
			if(a_sRemoteAddress == null) {
				a_oRegistry = LocateRegistry.getRegistry(a_sRemoteAddress, a_iRemotePort);
			} else if (a_oRegistry == null) {
				a_oRegistry = LocateRegistry.createRegistry(a_iRemotePort);
			}

			Registry oRegistry = a_oRegistry;
			Remote oStub = null;

			if(Fx_Contains(a_sExchange,oRegistry.list())) {
				System.out.printf("_WARNING: %slocal address/name for rmi already in use.\n", MLogManager.MethodName());
				Close();
			} else {
				oStub = UnicastRemoteObject.exportObject(a_oRemote, a_iRemotePort);
			}

			while (!IsClose()) {
				a_oTimeOut.Now();
				if(a_bIsRebind || !Fx_Contains(a_sExchange,oRegistry.list())) {
					oRegistry.rebind(a_sExchange, oStub);
				}
			}

			if(Fx_Contains(a_sExchange,oRegistry.list())) {
				oRegistry.unbind(a_sExchange);
			}

			UnicastRemoteObject.unexportObject(a_oRemote, a_bIsForceUnExport);
		} catch (Exception oException) {
			System.out.printf("_WARNING: %s@%s %s \"%s\"\n"
					,MLogManager.MethodName()
					,GetIdentifier().toString()
					,oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

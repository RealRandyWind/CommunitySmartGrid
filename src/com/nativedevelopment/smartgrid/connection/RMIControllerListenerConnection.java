package com.nativedevelopment.smartgrid.connection;

import com.nativedevelopment.smartgrid.Connection;
import com.nativedevelopment.smartgrid.IController;
import com.nativedevelopment.smartgrid.ISettings;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;

public class RMIControllerListenerConnection extends Connection{
	public static final String SETTINGS_KEY_EXCHANGE = "exchange";
	public static final String SETTINGS_KEY_LOCALPORT = "local.port";
	public static final String SETTING_KEY_ISREBIND = "isrebind";
	public static final String SETTING_KEY_ISFORCEUNEXPORT = "isforceunexport";

	public static final String SETTINGS_KEY_CHECKTIME = "checktime";

	protected static Registry a_oRegistry = null;

	private IController a_oRemote = null;
	private AbstractMap<Object, Remote> a_lRemotes = null;

	private String a_sExchange = null;
	private int a_nLocalPort = 0;
	private int a_nCheckTime = 0;
	private boolean a_bIsRebind = false;
	private boolean a_bIsForceUnExport = false;

	public RMIControllerListenerConnection(UUID oIdentifier, IController oRemote, Queue<Serializable> lToLogQueue) {
		super(oIdentifier, lToLogQueue);
		a_oRemote = oRemote;
	}

	private boolean Fx_Contains(String sBind, String[] lBinds) {
		// TODO do not know if proper way to check this
		return Arrays.asList(lBinds).contains(sBind);
	}

	@Override
	public void Configure(ISettings oConfigurations) {
		a_sExchange = oConfigurations.GetString(SETTINGS_KEY_EXCHANGE);
		a_nLocalPort = (int)oConfigurations.Get(SETTINGS_KEY_LOCALPORT);
		a_nCheckTime = (int)oConfigurations.Get(SETTINGS_KEY_CHECKTIME);
		a_bIsRebind = (boolean)oConfigurations.Get(SETTING_KEY_ISREBIND);
		a_bIsForceUnExport = (boolean)oConfigurations.Get(SETTING_KEY_ISFORCEUNEXPORT);
	}

	@Override
	public void Run() {
		try {
			if (a_oRegistry == null) {
				//TODO check may cause problem when having multiple servers running on current server/node
				a_oRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			}

			Registry oRegistry = a_oRegistry;
			Remote oStub = null;

			if(Fx_Contains(a_sExchange,oRegistry.list())) {
				System.out.printf("_WARNING: [RMIControllerListenerConnection] local address/name for rmi already in use.\n");
				Close();
			} else {
				oStub = UnicastRemoteObject.exportObject(a_oRemote, a_nLocalPort);
			}

			while (!IsClose()) {
				Thread.sleep(a_nCheckTime);
				if(a_bIsRebind || !Fx_Contains(a_sExchange,oRegistry.list())) {
					oRegistry.rebind(a_sExchange, oStub);
				}
			}

			if(Fx_Contains(a_sExchange,oRegistry.list())) {
				oRegistry.unbind(a_sExchange);
			}

			UnicastRemoteObject.unexportObject(a_oRemote, a_bIsForceUnExport);
		} catch (Exception oException) {
			System.out.printf("_WARNING: [RMIControllerListenerConnection.Run] %s \"%s\"\n",oException.getClass().getCanonicalName(),oException.getMessage());
		}
	}
}

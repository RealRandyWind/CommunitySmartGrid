package com.nativedevelopment.smartgrid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MConnectionManager {
    private static MConnectionManager a_oInstance = null;
    private boolean a_bIsSetUp = false;
    private boolean a_bIsShutDown = true;

    private MLogManager a_mLogManager = null;

    private Map<UUID, IConnection> a_lConnections = null;

    private MConnectionManager() {
        a_bIsShutDown = false;
        a_bIsSetUp = false;

        a_mLogManager = MLogManager.GetInstance();
    }

    public static MConnectionManager GetInstance() {
        if(a_oInstance != null) { return a_oInstance; }
        a_oInstance = new MConnectionManager();
        return a_oInstance;
    }

    public void SetUp() {
        if(a_bIsSetUp) {
            a_mLogManager.Warning("[MConnectionManager.SetUp] setup already.",0);
            return;
        }
        a_bIsShutDown = false;

        a_lConnections = new HashMap<>();
        // TODO MConnectionManager SetUp

        a_bIsSetUp = true;
        a_mLogManager.Success("[MConnectionManager.SetUp]",0);
    }

    public void ShutDown() {
        if(a_bIsShutDown) {
            a_mLogManager.Warning("[MConnectionManager.ShutDown] shutdown already.",0);
            return;
        }
        a_bIsSetUp = false;

        // TODO MConnectionManager ShutDown

        a_bIsShutDown = true;
        a_mLogManager.Success("[MConnectionManager.ShutDown]",0);
    }

    public void EstablishConnection(UUID iConnection) {
        IConnection oConnection = GetConnection(iConnection);
        if(oConnection == null) {
            return;
        } else if (oConnection.IsActive()) {
            a_mLogManager.Warning("[MConnectionManager.EstablishConnection] connection already active \"%s\".",0,iConnection);
            return;
        }

        oConnection.Open();
    }

    public void DisestablishConnection(UUID iConnection) {
        IConnection oConnection = GetConnection(iConnection);
        if(oConnection == null) {
            return;
        } else if (!oConnection.IsActive()) {
            a_mLogManager.Warning("[MConnectionManager.DisestablishConnection] connection already inactive \"%s\".",0,iConnection);
            return;
        }

        oConnection.Close();
    }

    public void AddConnection(IConnection oConnection) {
        a_lConnections.put(oConnection.GetIdentifier(), oConnection);
    }

    public void RemoveConnection(UUID iConnection) {
        if(!a_lConnections.containsKey(iConnection)) {
            a_mLogManager.Warning("[MConnectionManager.RemoveConnection] connection does not exist \"%s\".", 0, iConnection);
            return;
        }
        IConnection oConnection = a_lConnections.remove(iConnection);
        oConnection.Close();
    }

    public IConnection GetConnection(UUID iConnection) {
        if(!a_lConnections.containsKey(iConnection)) {
            a_mLogManager.Warning("[MConnectionManager.GetConnection] connection does not exist \"%s\".",0,iConnection);
            return null;
        }
        return a_lConnections.get(iConnection);
    }

    public Iterable<IConnection> GetAllConnections() {
        return a_lConnections.values();
    }
}

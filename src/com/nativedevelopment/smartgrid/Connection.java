package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public class Connection implements IConnection {
    public static final String SETTINGS_KEY_ROUTEID = "route.id";

    protected Thread a_oThread = null;

    private UUID a_oIdentifier = null;
    protected UUID a_iRoute = null;
    protected Queue<Serializable> a_lToLogQueue = null;
    volatile private boolean a_bIsClose = false;

    public Connection(UUID oIdentifier) {
        a_oThread = new Thread(this);
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
    }

    @Override
    public UUID GetIdentifier() {
        return a_oIdentifier;
    }

    @Override
    public void Open() {
        a_oThread.start();
    }

    @Override
    public void Close() {
        a_bIsClose = true;
    }

    @Override
    public void ForceClose() {
        a_oThread.interrupt();
        a_bIsClose = false;
    }

    @Override
    public void Join(long nTimeout) throws Exception {
        a_oThread.join(nTimeout);
    }

    @Override
    public boolean IsClose() {
        return a_bIsClose;
    }

    @Override
    public boolean IsActive() {
        return a_oThread.isAlive();
    }

    @Override
    public void Run() {
        System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
    }

    @Override
    public void run() {
        Run();
        a_bIsClose = false;
    }

    @Override
    public void Configure(ISettings oConfigurations) {
        System.out.printf("_WARNING: %snothing to configure\n",MLogManager.MethodName());
    }

    @Override
    public void SetToLogQueue(Queue<Serializable> lToLogQueue) {
        a_lToLogQueue = lToLogQueue;
    }

    @Override
    public void SetRoute(UUID iRoute) {
        a_iRoute = iRoute;
    }
}

package com.nativedevelopment.smartgrid;

import java.util.UUID;

public class Connection implements IConnection {
    public static final long THREAD_WAITBEFOREINTERUPT_TIME = 1500;

    protected Thread a_oThread = null;
    private UUID a_oIdentifier = null;
    volatile private boolean a_isClose = false;

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
        a_isClose = true;
    }

    @Override
    public void ForceClose() {
        a_oThread.interrupt();
        a_isClose = false;
    }

    @Override
    public boolean IsClose() {
        return a_isClose;
    }

    @Override
    public boolean IsActive() {
        return a_oThread.isAlive();
    }

    @Override
    public void Run() {
        System.out.printf("_WARNING: [Connection.Run] not yet implemented\n");
    }

    @Override
    public void run() {
        Run();
        a_isClose = false;
    }

    @Override
    public void Configure(ISettings oConfigurations) {
        System.out.printf("_WARNING: [Connection.Configurate] nothing to configure\n");
    }
}


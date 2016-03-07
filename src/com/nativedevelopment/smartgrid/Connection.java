package com.nativedevelopment.smartgrid;

import java.util.UUID;

public class Connection implements IConnection {
    protected Thread a_oThread = null;
    private UUID a_oIdentifier = null;

    public Connection(UUID oIdentifier) {
        a_oThread = new Thread(this);
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
    }

    public UUID GetIdentifier() {
        return a_oIdentifier;
    }

    public void Open() {
        a_oThread.start();
    }

    public void Close() {
        a_oThread.interrupt();
    }

    public boolean IsActive() {
        return a_oThread.isAlive();
    }

    public void Run() {
        System.out.printf("_WARNING: [Connection.Run] not yet implemented\n");
    }

    public void run() {
        Run();
    }
}


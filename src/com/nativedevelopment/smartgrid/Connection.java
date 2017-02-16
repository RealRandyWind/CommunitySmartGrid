package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class Connection implements IConnection {
    protected Thread a_oThread = null;
    protected Queue<Serializable> a_lToQueue = null;
    protected Queue<Serializable> a_lFromQueue = null;
    protected Queue<Serializable> a_lToLogQueue = null;
    //protected Queue<Serializable> a_lRemote = null;
    private UUID a_oIdentifier = null;
    volatile private boolean a_isClose = false;

    protected int a_nCheckTime = 0;
    protected int a_nCheckTimeLowerBound = 0;
    protected int a_nCheckTimeUpperBound = 0;
    protected int a_nDeltaCheckTime = 0;

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
    public void Join(long nTimeout) throws Exception {
        a_oThread.join(nTimeout);
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
        System.out.printf("_WARNING: %snot yet implemented\n",MLogManager.MethodName());
    }

    @Override
    public void run() {
        Run();
        a_isClose = false;
    }

    @Override
    public boolean TimeOutRoutine(boolean condition) throws InterruptedException {
        if (!condition) {
            a_nCheckTime = a_nCheckTimeLowerBound;
            return false;
        }
        TimeOut();
        a_nCheckTime += a_nDeltaCheckTime;
        a_nCheckTime = a_nCheckTime >= a_nCheckTimeUpperBound ? a_nCheckTimeUpperBound : a_nCheckTime;
        return true;
    }

    @Override
    public  void TimeOut() throws InterruptedException {
        Thread.sleep(a_nCheckTime);
    }

    @Override
    public void Configure(ISettings oConfigurations) {
        System.out.printf("_WARNING: %snothing to configure\n",MLogManager.MethodName());
    }

    @Override
    public void SetFromQueue(Queue<Serializable> lFromQueue) {
        a_lFromQueue = lFromQueue;
    }

    @Override
    public void SetToQueue(Queue<Serializable> lToQueue) {
        a_lToQueue = lToQueue;
    }

    @Override
    public void SetToLogQueue(Queue<Serializable> lToLogQueue) {
        a_lToLogQueue = lToLogQueue;
    }
}

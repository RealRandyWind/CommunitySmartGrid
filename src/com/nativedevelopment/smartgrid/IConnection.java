package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.Queue;
import java.util.UUID;

public interface IConnection extends Runnable, IConfigurable {
    public UUID GetIdentifier();
    public void Open();
    public void Close();
    public void ForceClose();
    public void Join(long nTimeout) throws Exception;
    public boolean IsClose();
    public boolean IsActive();
    public void Run();
    public boolean TimeOutRoutine(boolean condition) throws Exception;
    public void TimeOut() throws Exception;
    public void SetFromQueue(Queue<Serializable> lFromQueue);
    public void SetToQueue(Queue<Serializable> lToQueue);
    public void SetToLogQueue(Queue<Serializable> lToLogQueue);
    public void SetRemoteQueue(Queue<Serializable> lRemoteQueue);
}
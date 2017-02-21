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
    public void SetToLogQueue(Queue<Serializable> lToLogQueue);
}
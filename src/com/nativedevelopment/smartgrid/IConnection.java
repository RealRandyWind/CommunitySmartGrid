package com.nativedevelopment.smartgrid;

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
}
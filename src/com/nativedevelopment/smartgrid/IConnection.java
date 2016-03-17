package com.nativedevelopment.smartgrid;

import java.util.UUID;

public interface IConnection extends Runnable, IConfigurable {
    public UUID GetIdentifier();
    public void Open();
    public void Close();
    public void ForceClose();
    public boolean IsClose();
    public boolean IsActive();
    public void Run();
}
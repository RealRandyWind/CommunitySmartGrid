package com.nativedevelopment.smartgrid;

import java.util.UUID;

public interface IConnection extends Runnable {
    public UUID GetIdentifier();
    public void Open();
    public void Close();
    public boolean IsActive();
    public void Run();
}
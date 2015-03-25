package com.nativedevelopment.smartgrid;

import com.nativedevelopment.smartgrid.Action;

public interface IClient {
    public void passActionToDevice(Action action);
};
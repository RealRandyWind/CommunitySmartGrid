package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IAction extends Serializable {
    public UUID GetIdentifier();
    public Serializable[] GetParameters();
}

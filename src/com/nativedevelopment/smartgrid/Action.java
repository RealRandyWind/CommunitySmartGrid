package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Action implements IAction{
    private UUID a_oIdentifier = null;
    private Serializable[] a_lParameters = null;

    public Action(UUID oIdentifier, Serializable[] lParameters) {
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
        a_lParameters = lParameters;
    }

    public UUID GetIdentifier() {
        return a_oIdentifier;
    }

    public Serializable[] GetParameters() {
        return  a_lParameters;
    }
}

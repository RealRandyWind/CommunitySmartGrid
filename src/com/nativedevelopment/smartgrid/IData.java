package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IData extends Serializable {
    public String[] GetAttributes();
    public Serializable[] GetTuple(int iTuple);
    public Serializable[][] GetAllTuples();
    public UUID GetIdentifier();
    public int GetFlag();
}

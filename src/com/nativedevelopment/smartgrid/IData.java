package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IData extends Serializable {
    public UUID GetIdentifier();
    public String[] GetAttributes();
    public Serializable[] GetTuple(int iTuple);
    public Serializable[][] GetAllTuples();
}

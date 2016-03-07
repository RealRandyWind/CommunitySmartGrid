package com.nativedevelopment.smartgrid;

import java.io.Serializable;

public interface IData extends Serializable {
    public String[] GetAttributes();
    public Serializable[] GetTuple(int iTuple);
    public Serializable[][] GetAllTuples();
}

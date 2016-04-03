package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Data implements IData {
    private Serializable[][] a_lTuples = null;
    private String[] a_lAttributes = null;

    public Data(Serializable[][] lTuples, String[] lAttributes) {
        a_lAttributes = lAttributes;
        a_lTuples = lTuples;
    }

    @Override
    public String[] GetAttributes() {
        return  a_lAttributes;
    }

    @Override
    public Serializable[] GetTuple(int iTuple) {
        if (iTuple < 0 || iTuple >= a_lTuples.length) {
            return  null;
        }
        return a_lTuples[iTuple];
    }

    @Override
    public Serializable[][] GetAllTuples() {
        return a_lTuples;
    }
}

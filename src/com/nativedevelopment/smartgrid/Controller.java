package com.nativedevelopment.smartgrid;

public class Controller implements IController{
    protected static Controller a_oInstance = null;

    protected Controller() {
    }

    public static Controller GetInstance() {
        if(a_oInstance != null) { return a_oInstance; }
        a_oInstance = new Controller();
        return a_oInstance;
    }
}

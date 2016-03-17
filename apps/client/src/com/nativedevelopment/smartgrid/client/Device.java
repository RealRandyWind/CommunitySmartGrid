package com.nativedevelopment.smartgrid.client;

import com.nativedevelopment.smartgrid.IAction;
import com.nativedevelopment.smartgrid.IConnection;

import java.util.UUID;

public class Device implements IDevice {
    private UUID a_oIdentifier = null;
    private boolean a_bIsAllowControl = false;
    private String[] a_lDataSpace = null;
    private UUID[] a_lActionSpace = null;

    public Device(UUID oIdentifier, String[] lDataSpace, UUID[] lActionSpace) {
        if(oIdentifier == null) { oIdentifier = UUID.randomUUID(); }
        a_oIdentifier = oIdentifier;
        a_lActionSpace = lActionSpace;
        a_lDataSpace = lDataSpace;
    }

    public UUID GetIdentifier() {
        return a_oIdentifier;
    }

    public String[] GetDataSpace() {
        return a_lDataSpace;
    }

    public UUID[] GetActionSpace() {
        return  a_lActionSpace;
    }

    public void Control(IAction oAction) {
        System.out.printf("_WARNING: [Device.Control] not yet implemented\n");
    }

    public boolean IsAllowControl() {
        return a_bIsAllowControl;
    }

    public void SetAllowControl(boolean bIsAllowControl) {
        a_bIsAllowControl = bIsAllowControl;
    }
}

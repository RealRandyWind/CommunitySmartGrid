package com.nativedevelopment.smartgrid;

import java.util.UUID;

public interface IControlable {
    public UUID[] GetActionSpace();
    public void Control(IAction oAction);
    public boolean IsAllowControl();
    public void SetAllowControl(boolean bIsAllowControl);
}

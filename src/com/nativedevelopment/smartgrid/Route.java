package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Route implements IRoute {
    private UUID a_iRoute = null;
    private Serializable a_oContent = null;

    public Route(UUID iRoute, Serializable oContent) {
        a_iRoute = iRoute;
        SetContent(oContent);
    }

    @Override
    public UUID GetRouteId() {
        return a_iRoute;
    }

    @Override
    public Serializable GetContent() {
        return a_oContent;
    }

    @Override
    public IRoute SetContent(Serializable oContent) {
        a_oContent = oContent;
        return this;
    }
}

package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public class Route implements IRoute {
    private UUID a_iRoute = null;
    private Serializable a_oContent = null;

    public Route(UUID iRoute, Serializable oContent) {
        a_iRoute = iRoute;
        a_oContent = oContent;
    }

    public UUID GetRouteId() {
        return a_iRoute;
    }

    public Serializable GetContent() {
        return a_oContent;
    }
}

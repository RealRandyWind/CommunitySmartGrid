package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IRoute extends Serializable {
    public UUID GetRouteId();
    public Serializable GetContent();
}

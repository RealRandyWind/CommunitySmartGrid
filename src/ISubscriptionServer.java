package com.nativedevelopment.smartgrid;

import com.nativedevelopment.smartgrid.IClient;

import java.util.UUID;

public interface ISubscriptionServer {
    public IClient getClient(UUID clientId);
};

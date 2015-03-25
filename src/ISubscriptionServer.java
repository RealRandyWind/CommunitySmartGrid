package com.nativedevelopment.smartgrid;

import com.nativedevelopment.smartgrid.IClient;

public interface ISubscriptionServer {
    public IClient getClient(String clientId);
};

package com.nativedevelopment.smartgrid;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import com.nativedevelopment.smartgrid.MLogManager;
import com.nativedevelopment.smartgrid.IClient;

public class FakeSubscriptionServer {
    private MLogManager mLogManager = MLogManager.GetInstance();
    public static FakeSubscriptionServer instance = new FakeSubscriptionServer();

    public IClient getClient(UUID clientId) {
        try {
            InetAddress ip = Inet4Address.getLocalHost();
            return new com.nativedevelopment.smartgrid.FakeClient(ip);
        } catch (UnknownHostException e) {
            mLogManager.Error(e.getMessage(), 0);
            mLogManager.Error("This is fatal. Good bye!", -1);
            System.exit(-1);
        }
        return null;
    }
}

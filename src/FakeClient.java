package com.nativedevelopment.smartgrid;

/**
 * Client that is the real client, but instead communicates over the network.
 */
public class FakeClient implements com.nativedevelopment.smartgrid.IClient{

    public FakeClient(String clientId) {

    }

    @Override
    public void passActionToDevice(com.nativedevelopment.smartgrid.Action action) {

    }
}

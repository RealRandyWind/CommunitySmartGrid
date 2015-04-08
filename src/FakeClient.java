package com.nativedevelopment.smartgrid;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Client that is the real client, but instead communicates over the network.
 */
public class FakeClient implements com.nativedevelopment.smartgrid.IClient {
    private InetAddress ip;

    public FakeClient(InetAddress ip) {
        this.ip = ip;
    }

    @Override
    public void passActionToDevice(Action action) {
        // doet RMI oid en stuurt het naar de client gehost op this.ip
    }

	@Override
	public UUID getIdentifier() {
		return null;
	}
}

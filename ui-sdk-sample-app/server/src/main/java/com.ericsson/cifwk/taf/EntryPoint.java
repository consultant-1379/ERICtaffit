package com.ericsson.cifwk.taf;

import by.stub.client.StubbyClient;
import by.stub.server.JettyFactory;

public class EntryPoint {

    public static void main(String[] args) throws Exception {
        Config config = Config.newInstance();
        if (config.isValid()) {
            startStubbedBackend(config);
            while (true) {
                Thread.sleep(1000);
            }
        } else {
            System.out.println("No path to yaml config specified");
        }
    }

    private static void startStubbedBackend(Config config) throws Exception {
        StubbyClient stubbyClient = new StubbyClient();
        stubbyClient.startJetty(config.getBackendPort(), JettyFactory.DEFAULT_SSL_PORT, JettyFactory.DEFAULT_ADMIN_PORT, config.getBackendHost(), config.getPathToYaml());
    }
}

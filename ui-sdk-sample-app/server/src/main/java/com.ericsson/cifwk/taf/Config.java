package com.ericsson.cifwk.taf;

import by.stub.server.JettyFactory;

public class Config {
    public static final String YAML_PATH = "yaml";
    public static final String BACKEND_HOST = "bhost";
    public static final String BACKEND_PORT = "bport";

    private final String backendHost;
    private final int backendPort;
    private final String pathToYaml;

    private Config() {
        backendHost = System.getProperty(BACKEND_HOST, "0.0.0.0");
        backendPort = Integer.parseInt(System.getProperty(BACKEND_PORT, "" + JettyFactory.DEFAULT_STUBS_PORT));
        pathToYaml = System.getProperty(YAML_PATH);
    }

    public static Config newInstance() {
        return new Config();
    }

    public boolean isValid() {
        return pathToYaml != null;
    }

    public String getBackendHost() {
        return backendHost;
    }

    public String getPathToYaml() {
        return pathToYaml;
    }

    public int getBackendPort() {
        return backendPort;
    }
}

package com.ericsson.cifwk.taf;

import by.stub.client.StubbyClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class ServerTest {

    @Before
    public void setUp() throws Exception {
        StubbyClient stubbyClient = new StubbyClient();
        System.out.println("Current path:" + new File("").getAbsolutePath());
        stubbyClient.startJetty("ui-sdk-sample-app/server/src/main/resources/responses/response-mocks.yaml");
    }

    /**
     * This is manual test, run only for local mock server start
     *
     * @throws InterruptedException
     */
    @Test
    @Ignore
    public void idle() throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
        }
    }
}

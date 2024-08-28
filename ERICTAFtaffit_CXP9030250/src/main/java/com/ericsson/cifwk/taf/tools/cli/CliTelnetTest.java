package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.utils.FileFinder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.google.common.truth.Truth.assertThat;

public class CliTelnetTest extends TafTestBase {


    private Shell telnetShell;

    @BeforeMethod
    public void setUp() throws IOException {
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_005")
    public void testTelnetShell() {
        TestDataSource<DataRecord> dataSource = fromClass(TelnetRequestDataSource.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("TelnetRequests")
                .addFlow(flow("TelnetRequests")
                        .addTestStep(annotatedMethod(this, "open-telnet"))
                        .addTestStep(annotatedMethod(this, "perform-request"))
                        .addTestStep(annotatedMethod(this, "verify-response"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @TestStep(id = "open-telnet")
    public void openTelnet(@Input("host") Host host) {
        CLI commandHelper = new CLI(host);
        telnetShell = commandHelper.openTelnetShell(80);
    }

    @TestStep(id = "perform-request")
    public void writeRequest(@Input("request") String request, @Input("host") Host host) {
        telnetShell.write(request + "\r\n");
        telnetShell.write("host: " + host.getIp() + "\r\n");
        telnetShell.write("\n");

        FileFinder.findFile("test1.py");
    }

    @TestStep(id = "verify-response")
    public void writeRequest(@Input("response") String request) {
        String response = telnetShell.read();
        assertThat(response).startsWith("HTTP/1.1 200 OK");
    }

    @AfterMethod
    public void tearDown() {
        telnetShell.disconnect();
    }

    public static class TelnetRequestDataSource {
        @DataSource
        public List<Map<String, Object>> records() throws IOException {
            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, Object> record = new HashMap<>();

            record.put("host", TafDataHandler.findHost().withHostname("Google").get());
            record.put("request", "GET / HTTP/1.1");
            record.put("response", "HTTP/1.1 200 OK");
            records.add(record);

            return records;
        }
    }
}

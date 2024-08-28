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
import com.google.common.io.Resources;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.cifwk.taf.Constants.UISDK_VAPP_HOST;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CliRemoteScriptExecutionTest extends TafTestBase {

    private CLICommandHelper commandHelper;

    @BeforeMethod
    public void setUp() throws IOException {
        Host gatewayHost = TafDataHandler.findHost().withHostname(UISDK_VAPP_HOST).get();
        commandHelper = new CLICommandHelper(gatewayHost);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_004")
    public void testRemoteScriptExecution() {
        TestDataSource<DataRecord> dataSource = fromClass(ScriptExecutionDataSource.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("RemoteScriptExecutionScenario")
                .addFlow(flow("ExecuteRemoteScript")
                        .addTestStep(annotatedMethod(this, "create-script-on-remote-host"))
                        .addTestStep(annotatedMethod(this, "execute-script-with-command"))
                        .addTestStep(annotatedMethod(this, "delete-folder-and-script-from-remote-host"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @TestStep(id = "create-script-on-remote-host")
    public void createScriptOnRemoteHost(@Input("scriptName") String name, @Input("scriptFolder") String scriptFolder, @Input("scriptContent") String scriptContent) {
        String fullPath = scriptFolder + "/" + name;
        commandHelper.simpleExec("mkdir " + scriptFolder);
        commandHelper.simpleExec("echo '" + scriptContent + "' > " + fullPath);
        commandHelper.simpleExec("chmod +x " + fullPath);
        commandHelper.execute("cd " + scriptFolder);
    }

    @TestStep(id = "execute-script-with-command")
    public void executeScript(@Input("scriptName") String name, @Input("scriptResult") String expectedResult) {
        String command = "sh " + name;
        String s = commandHelper.execute(command);
        assertThat(s, containsString(expectedResult));
    }

    @TestStep(id = "delete-folder-and-script-from-remote-host")
    public void deleteScriptOnRemoteHost(@Input("scriptFolder") String scriptFolder) {
        commandHelper.execute("cd");
        commandHelper.execute("rm -rf " + scriptFolder);
        int commandExitValue = commandHelper.getCommandExitValue();
        assertThat(commandExitValue, is(0));
    }

    @AfterMethod
    public void tearDown() {
        commandHelper.disconnect();
    }

    public static class ScriptExecutionDataSource {
        @DataSource
        public List<Map<String, Object>> records() throws IOException {
            URL resource = Resources.getResource("scripts/cli-tool-remote-script.sh");
            String scriptContent = Resources.toString(resource, Charset.defaultCharset());

            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, Object> record = new HashMap<>();
            record.put("scriptContent", scriptContent);
            record.put("scriptName", "TAF_CLI_Tool_Func_004_test_script.sh");
            record.put("scriptFolder", "test-scripts");
            record.put("scriptResult", "Output of same_file.txt contents\n" +
                    "Creating text file\n" +
                    "Appended String");
            records.add(record);

            return records;
        }
    }
}

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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class CliToolEnvironmentVariablesUseTest extends TafTestBase {

    private CLICommandHelper commandHelper;
    private Shell shell;

    @BeforeMethod
    public void setUp() {
        Host gatewayHost = TafDataHandler.findHost().withHostname(UISDK_VAPP_HOST).get();
        commandHelper = new CLICommandHelper(gatewayHost);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_002")
    public void testEnvironmentVariablesAreReset() {
        TestDataSource<DataRecord> dataSource = fromClass(EnvVariablesDataSource.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("EnvironmentVariablesAreReset")
                .addFlow(flow("ExecuteCommandsWithEnvironmentVariables")
                        .addTestStep(annotatedMethod(this, "open-shell"))
                        .addTestStep(annotatedMethod(this, "check-environment-variable"))
                        .addTestStep(annotatedMethod(this, "set-environment-variable"))
                        .addTestStep(annotatedMethod(this, "execute-command-when-variable-is-set"))
                        .addTestStep(annotatedMethod(this, "reset-environment-variable"))
                        .addTestStep(annotatedMethod(this, "check-environment-variable"))
                        .addTestStep(annotatedMethod(this, "execute-command-when-variable-is-unset"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @TestStep(id = "open-shell")
    public void openShell() {
        shell = commandHelper.openShell();
    }

    @TestStep(id = "check-environment-variable")
    public void checkEnvironmentVariable(@Input("variableName") String variableName, @Input("unsetVariableValue") String expectedValue) {
        String env = shell.getEnv(variableName);
        assertThat(env, is(expectedValue));
    }

    @TestStep(id = "set-environment-variable")
    public void setEnvironmentVariable(@Input("variableName") String variableName, @Input("envVariableValue") String variableValue) {
        shell.setEnv(variableName, variableValue);
    }

    @TestStep(id = "execute-command-when-variable-is-set")
    public void executeCommandWhenVariableIsSet(@Input("command") String command, @Input("commandResultWithSetVariable") String expectedOutput, @Input("envVariableValue") String envVariableValue) {
        String output = commandHelper.execute(command);
        assertThat(output, containsString(expectedOutput));
        assertThat(output, containsString(envVariableValue));
    }

    @TestStep(id = "execute-command-when-variable-is-unset")
    public void executeCommandWhenVariableIsUnset(@Input("command") String command, @Input("commandResultWithUnsetVariable") String expectedOutput, @Input("envVariableValue") String envVariableValue) {
        String output = commandHelper.execute(command);
        assertThat(output, containsString(expectedOutput));
        assertThat(output, not(containsString(envVariableValue)));
    }

    @TestStep(id = "reset-environment-variable")
    public void resetEnvironmentVariable() {
        shell.resetEnv();
    }

    @AfterMethod
    public void tearDown() {
        shell.disconnect();
    }

    public static class EnvVariablesDataSource {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, Object> record = new HashMap<>();
            record.put("variableName", "ENVIRONMENT");
            record.put("unsetVariableValue", null);
            record.put("envVariableValue", "TEST");
            record.put("command", "echo \"Script is executed in $ENVIRONMENT\"");
            record.put("commandResultWithSetVariable", "Script is executed in TEST");
            record.put("commandResultWithUnsetVariable", "Script is executed in");
            records.add(record);

            record = new HashMap<>();
            record.put("variableName", "WIKIPEDIA");
            record.put("unsetVariableValue", null);
            record.put("envVariableValue", "wikipedia.com");
            record.put("command", "echo \"Article is not found in $WIKIPEDIA\"");
            record.put("commandResultWithSetVariable", "Article is not found in wikipedia.com");
            record.put("commandResultWithUnsetVariable", "Article is not found in");
            records.add(record);

            return records;
        }
    }
}

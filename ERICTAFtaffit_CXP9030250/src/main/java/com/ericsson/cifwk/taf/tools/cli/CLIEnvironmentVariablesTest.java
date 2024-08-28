package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class CLIEnvironmentVariablesTest extends TafTestBase {

    Shell shell;

    private static Host host;

    @BeforeMethod
    public void setUp() throws Exception {
        host = TafDataHandler.findHost().withHostname("netsim").get();
        CLI cli = new CLI(host);
        shell = cli.openShell();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_013")
    public void shouldBeReturnAllEnvironmentVariables() throws Exception {
        Map<String, String> envs = shell.getEnv();
        assertThat(envs.size()).isGreaterThan(0);
        String hostName = envs.get("HOSTNAME");
        assertThat(host.getHostname()).isEqualTo(hostName);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_014")
    public void shouldBeReturnNullForWrongName() throws Exception {
        String env = shell.getEnv("WRONG_NAME_" + System.currentTimeMillis());
        assertThat(env).isNull();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_015")
    public void shouldBeReturnEnvironmentVariable() throws Exception {
        String hostName = shell.getEnv("HOSTNAME");
        assertThat(host.getHostname()).isEqualTo(hostName);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_016")
    public void shouldBeSetEnvironmentVariable() throws Exception {
        long time = System.currentTimeMillis();
        String name = "TEST" + time;
        String value = "" + time;
        shell.setEnv(name, value);
        String env = shell.getEnv(name);
        assertThat(value).isEqualTo(env);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_017")
    public void shouldBeUnsetEnvironmentVariable() throws Exception {
        long time = System.currentTimeMillis();
        String name = "TEST_" + time;
        shell.setEnv(name, "" + time);
        shell.unsetEnv(name);
        assertThat(shell.getEnv(name)).isNull();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_018")
    public void shouldResetEnvironmentVariableToLoginState() throws Exception {
        shell.resetEnv();
        Map<String, String> before = shell.getEnv();
        long time = System.currentTimeMillis();
        shell.setEnv("TEST_" + time, "" + time);
        shell.setEnv("HOSTNAME", "" + time);
        shell.resetEnv();
        Map<String, String> after = shell.getEnv();
        assertThat(before.size()).isEqualTo(after.size());
        assertThat(after.get("TEST_" + time)).isNull();
        assertThat(before.get("HOSTNAME")).isEqualTo(after.get("HOSTNAME"));
        for (Map.Entry<String, String> env : after.entrySet()) {
            assertThat(before.get(env.getKey())).isEqualTo(env.getValue());
        }
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (shell != null) {
            shell.disconnect();
        }

    }

}

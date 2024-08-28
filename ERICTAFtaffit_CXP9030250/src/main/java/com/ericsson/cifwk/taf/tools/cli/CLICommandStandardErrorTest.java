package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

/**
 * CIP-5427 - Cannot get standard error from a CLI Command
 *
 * @see <a href="http://jira-oss.lmera.ericsson.se/browse/CIP-5427">CIP-5427</a>
 */
public class CLICommandStandardErrorTest {

    CLI cli;
    Shell shell;

    @Before
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("ms1").get();
        cli = new CLI(host);
    }

    @After
    public void tearDown() throws Exception {
        if (shell != null) shell.disconnect();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_060")
    public void execute_shouldProduce_STDERR_when_Terminal_is_NULL() throws TimeoutException {
        shell = cli.executeCommand(Terminal.NULL, "echo STDERR >&2", "wrong_command");
        String stdout = shell.read();
        String stderr = shell.readErr();
        assertThat(stdout, isEmptyString());
        assertThat(stderr, containsString("STDERR"));
        assertThat(stderr, containsString("wrong_command"));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_061")
    public void execute_notProduce_STDERR_when_Terminal_is_defined() throws TimeoutException {
        shell = cli.executeCommand(Terminal.VT100, "echo STDERR >&2", "wrong_command");
        String stdout = shell.read();
        String stderr = shell.readErr();
        assertThat(stdout, containsString("STDERR"));
        assertThat(stdout, containsString("wrong_command"));
        assertThat(stderr, isEmptyString());
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_062")
    public void shell_shouldProduce_STDERR_when_Terminal_is_NULL() throws TimeoutException {
        shell = cli.openShell(Terminal.NULL);
        shell.writeln("echo STDERR >&2");
        shell.writeln("wrong_command");
        shell.writeln("exit");
        String stdout = shell.read(2);
        String stderr = shell.readErr(2);
        assertThat(stdout, isEmptyString());
        assertThat(stderr, containsString("STDERR"));
        assertThat(stderr, containsString("wrong_command"));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_063")
    public void shell_notProduce_STDERR_when_Terminal_is_defined() throws TimeoutException {
        shell = cli.openShell(Terminal.VT100);
        shell.writeln("echo STDERR >&2");
        shell.writeln("wrong_command");
        shell.writeln("exit");
        String stdout = shell.read(2);
        String stderr = shell.readErr(2);
        assertThat(stdout, containsString("STDERR"));
        assertThat(stdout, containsString("wrong_command"));
        assertThat(stderr, isEmptyString());
    }

}

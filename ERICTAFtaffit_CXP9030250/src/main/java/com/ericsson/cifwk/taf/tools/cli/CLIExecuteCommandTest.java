package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class CLIExecuteCommandTest extends TafTestBase {

    public static final String HOST_FOLDER = "/usr/tmp/";

    CLI cli;
    Shell shell;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        cli = new CLI(host);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_019")
    public void helloWorldTest() throws TimeoutException {
        shell = cli.executeCommand(Terminal.VT100, "echo \"Hello World!\"");
        shell.expect("Hello World!");
        shell.expectClose();
        assertThat(shell.getExitValue()).isEqualTo(0);
        assertThat(shell.isClosed()).isTrue();
    }

    /**
     * Execute commands chain
     */
    @Test
    @TestId(id = "TAF_CLI_Tool_Func_020")
    public void expectRemoteExecution_string_multiple_command_as_one_execute() throws Exception {
        shell = cli.executeCommand(Terminal.VT100, "read -p \"Enter text:\" text ; echo \"Text:$text\"");
        shell.expect("Enter text:");
        shell.writeln("T3_TEST_MESSAGE");
        shell.expect("Text:T3_TEST_MESSAGE");
        shell.expectClose();
        assertThat(shell.getExitValue()).isEqualTo(0);
        assertThat(shell.isClosed()).isTrue();
    }

    /**
     * Execute commands chain
     */
    @Test
    @TestId(id = "TAF_CLI_Tool_Func_021")
    public void expectRemoteExecution_multiple_command_as_one_execute() throws Exception {
        shell = cli.executeCommand(Terminal.VT100,
                                   "read -p \"Enter text:\" text",
                                   "echo \"Text:$text\"");
        shell.expect("Enter text:");
        shell.writeln("T3_TEST_MESSAGE");
        shell.expect("Text:T3_TEST_MESSAGE");
        shell.expectClose();
        assertThat(shell.getExitValue()).isEqualTo(0);
        assertThat(shell.isClosed()).isTrue();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_022")
    public void shouldReturnExitValue_whenProcessFinished() throws TimeoutException {
        shell = cli.executeCommand(Terminal.VT100, "wrong_command");
        assertThat(shell.getExitValue()).isEqualTo(127);
        shell = cli.executeCommand(Terminal.VT100, "wrong_command","echo 'exitcode:'$?");
        shell.expect("exitcode:127");
        assertThat(shell.getExitValue()).isEqualTo(0);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (shell != null) shell.disconnect();
    }

}

package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

public class CLIShellTest extends TafTestBase {

    Shell shell;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        CLI cli = new CLI(host);
        shell = cli.openShell(Terminal.VT100);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (shell != null) shell.disconnect();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_025")
    public void helloWorldTest() throws Exception {
        shell.writeln("echo 'Hello World!'");
        shell.expect("Hello World!");
        shell.writeln("echo \"Hello from TAF CLI Tool\"");
        shell.expect("TAF CLI Tool");
        shell.writeln("exit");
        shell.expectClose();
        assertThat(shell.isClosed()).isTrue();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_026")
    public void testExitValue() throws Exception {
        shell.writeln("wrong_command");
        try {
            System.out.println(shell.getExitValue());
            fail();
        } catch (Exception ignore) {
        }
        shell.writeln("exit");
        shell.expectClose();
        assertThat(shell.isClosed()).isTrue();
        assertThat(shell.getExitValue()).isEqualTo(127);
    }

}

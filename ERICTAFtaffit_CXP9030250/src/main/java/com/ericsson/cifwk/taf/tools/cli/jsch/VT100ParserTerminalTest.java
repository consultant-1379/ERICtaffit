package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.Terminal;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class VT100ParserTerminalTest extends TafTestBase {

    Shell shell;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = DataHandler.getHostByName("netsim");
        CLI cli = new CLI(host);
        shell = cli.openShell(Terminal.VT100);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (shell != null) shell.disconnect();

    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_040")
    public void testLSCommand_shouldRemove_CSIColorCodes() throws Exception {
        shell.writeln("ls");
        System.out.println(shell.read());
        shell.writeln("exit");
        shell.expectClose();
        assertThat(shell.isClosed()).isTrue();
    }

}

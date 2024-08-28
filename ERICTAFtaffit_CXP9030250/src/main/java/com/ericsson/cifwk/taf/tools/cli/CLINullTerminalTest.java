package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class CLINullTerminalTest extends TafTestBase {

    Shell shell;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("training1").get();
        CLI cli = new CLI(host);
        shell = cli.openShell(Terminal.NULL);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_024")
    public void retrieveStdOut() throws Exception {
        shell.writeln("locate terminfo");
        assertThat(shell.read()).contains("/usr/share/terminfo");
    }

    @AfterMethod
    public void shutDown() throws Exception{
        if(shell!= null) shell.disconnect();
    }
}

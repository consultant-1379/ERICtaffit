package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.regex.Pattern;


public class CLINetsimTest extends TafTestBase {

    Shell shell;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = TafDataHandler.findHost().withHostname("netsim").get();
        CLI cli = new CLI(host, host.getUser("netsim"));
        shell = cli.openShell(Terminal.VT100);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_023")
    public void testPromptforParticularTerminalType() throws Exception {
        shell.writeln("cd inst;./netsim_shell");
        shell.writeln(".show simulations");
        String out = shell.expect(Pattern.compile("^default"));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (shell != null) shell.disconnect();

    }


}

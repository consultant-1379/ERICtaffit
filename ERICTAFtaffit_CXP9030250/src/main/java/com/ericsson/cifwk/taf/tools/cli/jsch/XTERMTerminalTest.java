package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.Terminal;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;


public class XTERMTerminalTest extends TafTestBase {

    Shell shell;
    String NEWLINE = "" + (char) 13 + (char) 10;

    @BeforeMethod
    public void setUp() throws Exception{
        Host host = DataHandler.getHostByName("netsim");
        CLI cli = new CLI(host);
        shell = cli.openShell(Terminal.XTERM);
    }

    @AfterMethod
    public void tearDown() throws Exception{
        if (shell != null)
            shell.disconnect();

    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_041")
    public void shouldgetHelloWorld() throws Exception{

        shell.writeln("echo -e \"\\x1b\\x5d\\x3bHello World\"");
        shell.expect("Hello World");

        shell.writeln("echo -e \"\\x1b\\x5d\\x9cHello World\"");
        shell.expect("Hello World");

        shell.writeln("echo -e \"\\x1b\\x30\\x30Hello World\"");
        shell.expect("0Hello World");

        shell.writeln("echo -e \"\\x1b[4mHello\\x1b[0m World\"");
        shell.expect("Hello World");

        shell.writeln("exit");
        shell.expectClose();
        assertThat(shell.isClosed()).isTrue();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_042")
    public void should_change_to_GROUND_State() throws Exception{
        // Parser goes to state:OSC_STRING and goes back to GROUND state
        shell.writeln("echo -e \"\\x1b\\x5d\\x3bHello World\"");
        String result = shell.read();
        try {
            if (!result.contains("echo -e \"\\x1b\\x5d\\x3bHello World\""
                    + NEWLINE + "Hello World"))
                fail();
        } catch (TimeoutException e) {
            // do Nothing
        }
        shell.writeln("exit");
        shell.expectClose();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_043")
    public void should_be_in_OSC_STRING_State() throws Exception{
        // Parser goes to state:OSC_STRING and hangs there
        shell.writeln("echo -e \"\\x1b\\x5d\\x3cHello World\"");
        String result = shell.read();
        try {
            if (result.contains("echo -e \"\\x1b\\x5d\\x3bHello World\""
                    + NEWLINE + "Hello World"))
                fail();
        } catch (TimeoutException e) {
            // do Nothing
        }
        shell.writeln("exit");
        shell.expectClose();
    }

}

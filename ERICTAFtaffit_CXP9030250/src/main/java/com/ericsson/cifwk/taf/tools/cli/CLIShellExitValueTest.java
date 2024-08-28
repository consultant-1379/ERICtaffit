package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CLIShellExitValueTest {

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
    @TestId(id = "TAF_CLI_Tool_Func_059")
    public void shouldThrowException_whenProcessNotFinished() throws TimeoutException {
        shell = cli.executeCommand(Terminal.VT100,
                "read -p \"Enter text:\" text",
                "wrong_command");
        try {
            assertEquals(127, shell.getExitValue());
            fail();
        } catch (TimeoutException ignore) {}
        shell.writeln("T3_TEST_MESSAGE");
        assertEquals(127, shell.getExitValue());
    }


}

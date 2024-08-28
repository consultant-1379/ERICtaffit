package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

public class CLITest extends TafTestBase {

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_027")
    public void shouldExecuteTheCommand() {
        Host masterHost = TafDataHandler.findHost().withHostname("netsim").get();
        CLI cli = new CLI(masterHost);
        Shell shell = cli.executeCommand("echo \"Hello World\"");
        String result = shell.read(10);
        assertThat(result).contains("Hello World");
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_028")
    public void shouldThrowAuthExceptionOnWrongPassword() {
        Host masterHost = TafDataHandler.findHost().withHostname("netsim").get();
        User wrongUser = new User("root", "incorrectpassowrd", UserType.ADMIN);
        CLI cli = new CLI(masterHost, wrongUser);
        try {
            Shell shell = cli.executeCommand("echo \"Hello World\"");
            String result = shell.read(10);
            fail("Auth Exception is expected");
        } catch (JSchCLIToolException e) {
            assertThat(true).isTrue();
        }
    }
}

package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class CommandHelperWrongBehavior {
    private CLICommandHelper cmdHelper;
    private Host host;

    @BeforeMethod
    public void setUp() throws Exception {
        host = TafDataHandler.findHost().withHostname("netsim").get();
        cmdHelper = new CLICommandHelper(host, host.getUsers(UserType.ADMIN).get(0));
    }

    @Test
    public void multipleCommandExecutionIncorrectBehaviorUsingSimpleExec() {
        cmdHelper.simpleExec("fqf", "ls");
        assertThat(cmdHelper.getCommandExitValue()).isEqualTo(0);
    }

    @Test(expectedExceptions = JSchCLIToolException.class)
    public void methodGetShellExitValueThrowExceptionAfterExitCommand() {
        cmdHelper.execute("exit");
        cmdHelper.getShellExitValue();
    }

    @Test(expectedExceptions = JSchCLIToolException.class)
    public void methodGetCommandExitValueThrowExceptionAfterExitCommand() {
        cmdHelper.execute("exit");
        cmdHelper.getCommandExitValue();
    }

    @AfterMethod
    public void tearDown() {
        cmdHelper.disconnect();
    }
}

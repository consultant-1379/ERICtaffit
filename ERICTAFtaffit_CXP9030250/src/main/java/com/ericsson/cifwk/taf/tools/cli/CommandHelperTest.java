package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class CommandHelperTest extends TafTestBase {

    CLICommandHelper cmdHelper;
    Host host;
    String scriptContent;

    @BeforeMethod
    public void setUp() throws Exception {
        host = TafDataHandler.findHost().withHostname("netsim").get();
        cmdHelper = new CLICommandHelper(host, host.getUsers(UserType.ADMIN).get(0));
        URL resource = Resources.getResource("scripts/matchMultipleLines.txt");
        scriptContent = Resources.toString(resource, Charset.defaultCharset());
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_029")
    public void verifySimpleExecExitCode() {
        cmdHelper.simpleExec("pwd", "ls -a", "du -h", "help");
        assertThat(cmdHelper.getCommandExitValue()).isEqualTo(0);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_030")
    public void verifyExitCodeOfSimpleExecIsBasedOnFinalCommand() {
        cmdHelper.simpleExec("help", "echo Hello", "env", "blahhhhhhhfahsdf");
        assertThat(cmdHelper.getCommandExitValue()).isEqualTo(127);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_031")
    public void verifyExitCodeFromExecuteCommand() {
        cmdHelper.openShell();
        cmdHelper.execute("hostname");
        assertThat(cmdHelper.getCommandExitValue()).isEqualTo(0);

        cmdHelper.closeAndValidateShell(10);
        assertThat(cmdHelper.getShellExitValue()).isEqualTo(0);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_032")
    public void verifyExitCodeAfterRunningInteractiveProcess() {
        User rootUser = new User("root", "shroot", UserType.ADMIN);
        cmdHelper.newHopBuilder().hop(rootUser);
        cmdHelper.interactWithShell("cd /root/");
        cmdHelper.interactWithShell("passwd netsim");
        cmdHelper.expect("New Password:");
        cmdHelper.interactWithShell("netsim");
        cmdHelper.expect("Reenter New Password:");
        cmdHelper.interactWithShell("netsim");
        cmdHelper.interactWithShell("exit");    //exit from root user
        assertThat(cmdHelper.getCommandExitValue()).isEqualTo(0);

        cmdHelper.closeAndValidateShell(10);
        assertThat(cmdHelper.getShellExitValue()).isEqualTo(0);
    }

    @Test
    public void verifyShellIsNullAfterCommandExecution() throws InterruptedException {
        cmdHelper.simpleExec("ls -a", "help", "pwd", "ps -ef", "echo hello");
        assertThat(cmdHelper.shell).isNull();
        cmdHelper.execute("ls -a");
        cmdHelper.closeAndValidateShell(10);
        assertThat(cmdHelper.getShellExitValue()).isEqualTo(0);
        cmdHelper.disconnect();
        assertThat(cmdHelper.shell).isNull();
    }

    @Test
    public void verifyShellCanBeChanged() {
        cmdHelper.openShell();
        cmdHelper.setShell(ShellType.BASH);
        cmdHelper.interactWithShell("echo $0");
        try {
            cmdHelper.expect("bash");
        } catch (TimeoutException e) {
            Assert.fail("bash not found on shell");
        }
        cmdHelper.setShell(ShellType.TCSH);
        cmdHelper.interactWithShell("echo $0");
        try {
            cmdHelper.expect("tcsh");
        } catch (TimeoutException e) {
            Assert.fail("tcsh not found on shell");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_044")
    public void verifyOutputForLongCommandsIsFilteredCorrectly() {
        cmdHelper.openShell();
        String output = cmdHelper.execute("echo Testing long commands are split correctly");
        Assert.assertThat(output.split("\n").length, is(2));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_045")
    public void verifyExpectMatchOverMultipleLines() {
        cmdHelper.openShell();
        cmdHelper.execute("echo -e '" + scriptContent + "' > matchMultipleLines.txt");
        cmdHelper.interactWithShell("cat matchMultipleLines.txt");
        try {
            cmdHelper.expect("tonight");
        } catch (TimeoutException e) {
            Assert.fail("tonight not found in file");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_046")
    public void verifyExpectMatchPattern() {
        cmdHelper.openShell();
        cmdHelper.execute("echo -e '" + scriptContent + "' > matchMultipleLines.txt");
        cmdHelper.interactWithShell("cat matchMultipleLines.txt");
        try {
            cmdHelper.expect(Pattern.compile("^In"));
        } catch (TimeoutException e) {
            Assert.fail("^In not found in file");
        }
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_047")
    public void verifySetAndGetEnv() {
        host = TafDataHandler.findHost().withHostname("training1").get();
        cmdHelper = new CLICommandHelper(host, host.getUsers(UserType.ADMIN).get(0));
        cmdHelper.setEnv("test", "testenv");
        String envs = cmdHelper.execute("env");
        assertThat(envs.contains("testenv")).isTrue();
        assertThat(cmdHelper.getEnv().get("test")).isEqualTo("testenv");
        assertThat(cmdHelper.getEnv("test")).isEqualTo("testenv");
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_049")
    public void shouldOpenShellInBackgroundWhenExecuteIsCalled() throws NullPointerException {
        cmdHelper.execute("help");
        assertThat(cmdHelper.getShell()).isNotNull();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_050")
    public void verifyOpenShellOpensNewShell() {
        cmdHelper.execute("help");
        Shell shell1 = cmdHelper.getShell();
        cmdHelper.openShell();
        Shell shell2 = cmdHelper.getShell();
        assertThat(shell1).isNotEqualTo(shell2);
    }

    @AfterMethod
    public void tearDown() {
        cmdHelper.disconnect();
    }
}

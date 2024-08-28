package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class JSchStdOutStdErrTest {

    Host host;
    Session session;
    Channel channel;
    Spawn spawn;
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() {
        host = DataHandler.getHostByName("ms1");
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(host.getUser(), host.getIp(), getPort(host));
            session.setPassword(host.getPass());
            // It must not be recommended, but if you want to skip host-key check,
            session.setConfig("StrictHostKeyChecking", "no");
            try{
                session.connect(1000);
            }catch(JSchException e) {
                LOGGER.error("session.connect failed. attempting second connection");
                session = jsch.getSession(host.getUser(), host.getIp(), getPort(host));
                session.setPassword(host.getPass());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect(1000);
                LOGGER.warn("session.connect succeeded on second attempt");
            }
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    static int getPort(Host host) {
        String port = host.getPort().get(Ports.SSH);
        if (port == null || port.trim().isEmpty()) return CLI.DEFAULT_SSH_PORT;
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            //"Wrong number format for SSH port [" + port + "] in host " + host.getHostname(), nfe);
            throw new CLIToolException(String.format("Wrong number format for SSH port {} in host {}", port, host.getHostname(), nfe));
        }
    }

    @After
    public void tearDown() {
        try {
            spawn.stop();
        } catch (Exception ignored) {
        }
        try {
            channel.disconnect();
        } catch (Exception ignored) {
        }
        try {
            session.disconnect();
        } catch (Exception ignored) {
        }
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_051")
    public void exec_shouldProduce_STDOUT_STDERR_bydefault() throws Exception {
        channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("echo STDOUT ; echo STDERR>&2;");
        spawn = Spawn.exec(((ChannelExec) channel), JSchCLITool.DEFAULT_TIMEOUT_SEC);
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, containsString("STDOUT"));
        assertThat(stderr, containsString("STDERR"));
        assertThat(spawn.getExitValue(), is(0));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_052")
    public void exec_notProduce_STDERR_when_PseudoTerminal_is_allocated() throws Exception {
        channel = session.openChannel("exec");
        ((ChannelExec) channel).setPty(true);
        ((ChannelExec) channel).setCommand("echo STDOUT ; echo STDERR>&2;");
        spawn = Spawn.exec(((ChannelExec) channel), JSchCLITool.DEFAULT_TIMEOUT_SEC);
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, containsString("STDOUT"));
        assertThat(stdout, containsString("STDERR"));
        assertThat(stderr, isEmptyString());
        assertThat(spawn.getExitValue(), is(0));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_053")
    public void exec_SUDO_throwError_bydefault() throws Exception {
        channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("echo ${sudoPwd} | sudo -S ls;");
        spawn = Spawn.exec(((ChannelExec) channel), JSchCLITool.DEFAULT_TIMEOUT_SEC);
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, isEmptyString());
        assertThat(stderr, containsString("you must have a tty to run sudo"));
        assertThat(spawn.getExitValue(), not(is(0)));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_054")
    public void exec_SUDO_execute_when_PseudoTerminal_is_allocated() throws Exception {
        channel = (ChannelExec) session.openChannel("exec");
        ((ChannelExec) channel).setPty(true);
        ((ChannelExec) channel).setCommand("echo ${sudoPwd} | sudo -S ls;");
        spawn = Spawn.exec(((ChannelExec) channel), JSchCLITool.DEFAULT_TIMEOUT_SEC);
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, not(isEmptyString()));
        assertThat(stderr, isEmptyString());
        assertThat(spawn.getExitValue(), is(0));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_054")
    public void shell_notProduce_STDERR_bydefault() throws Exception {
        channel = session.openChannel("shell");
        spawn = Spawn.shell((ChannelShell) channel, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        spawn.send("echo STDOUT\n");
        spawn.send("echo STDERR>&2\n");
        spawn.send("wrong-command\n");
        spawn.send("exit\n");
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, containsString("STDOUT"));
        assertThat(stdout, containsString("STDERR"));
        assertThat(stdout, containsString("wrong-command"));
        assertThat(stderr, isEmptyString());
        assertThat(spawn.getExitValue(), not(is(0)));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_055")
    public void shell_SUDO_shouldExecute_by_default() throws Exception {
        channel = session.openChannel("shell");
        spawn = Spawn.shell((ChannelShell) channel, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        spawn.send("echo ${sudoPwd} | sudo -S ls\n");
        spawn.send("exit\n");
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, not(containsString("you must have a tty to run sudo")));
        assertThat(stderr, isEmptyString());
        assertThat(spawn.getExitValue(), is(0));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_056")
    public void shell_produce_STDERR_when_PseudoTerminal_deallocated() throws Exception {
        channel = session.openChannel("shell");
        ((ChannelShell) channel).setPty(false);
        spawn = Spawn.shell((ChannelShell) channel, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        spawn.send("echo STDOUT\n");
        spawn.send("echo STDERR>&2\n");
        spawn.send("wrong-command\n");
        spawn.send("exit\n");
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, containsString("STDOUT"));
        assertThat(stderr, containsString("STDERR"));
        assertThat(stderr, containsString("wrong-command"));
        assertThat(spawn.getExitValue(), not(is(0)));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_057")
    public void exec_produce_STDERR_when_BASH() throws Exception {
        channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("/bin/bash");
        spawn = Spawn.exec((ChannelExec) channel, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        spawn.send("echo STDOUT\n");
        spawn.send("echo STDERR>&2\n");
        spawn.send("wrong-command\n");
        spawn.send("exit\n");
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, containsString("STDOUT"));
        assertThat(stderr, containsString("STDERR"));
        assertThat(stderr, containsString("wrong-command"));
        assertThat(spawn.getExitValue(), not(is(0)));
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_058")
    public void exec_SUDO_throwError_when_BASH() throws Exception {
        channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("/bin/bash");
        Spawn spawn = Spawn.exec((ChannelExec) channel, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        spawn.send("echo ${sudoPwd} | sudo -S ls\n");
        spawn.send("exit\n");
        String stdout = spawn.read();
        String stderr = spawn.readErr();
        spawn.expectClose();
        assertThat(stdout, isEmptyString());
        assertThat(stderr, containsString("you must have a tty to run sudo"));
        assertThat(spawn.getExitValue(), not(is(0)));
    }
}

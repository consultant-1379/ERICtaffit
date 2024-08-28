package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import expectj.ExpectJ;
import expectj.Spawn;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.fail;

public class JSchExecTest extends TafTestBase {

    Session session;
    ChannelExec channel;
    Spawn spawn;
    private ExpectJ expectJ;
    
    @BeforeMethod
    public void setUp() throws Exception {
        Host host = DataHandler.getHostByName("netsim");
        connect(host.getIp(), getPort(host), host.getUser(), host.getPass());
    }

    static int getPort(Host host) {
        String port = host.getPort().get(Ports.SSH);
        if (port == null || port.trim().isEmpty()) return CLI.DEFAULT_SSH_PORT;
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            throw new CLIToolException("Wrong number format for SSH port [" + port + "] in host " + host.getHostname(), nfe);
        }
    }

    void connect(String host, int port, String user, String password) throws JSchException, IOException {
        JSch jsch = new JSch();
        // jsch.setKnownHosts("/home/foo/.ssh/known_hosts");
        session = jsch.getSession(user, host, port);
        session.setPassword(password);
        // It must not be recommended, but if you want to skip host-key check, invoke following:
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(JSchCLITool.DEFAULT_TIMEOUT_SEC * 1000);   // making a connection with timeout millisecond.
        channel = (ChannelExec) session.openChannel("exec");
        expectJ = new ExpectJ(JSchCLITool.DEFAULT_TIMEOUT_SEC);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (spawn != null) spawn.stop();
        if (channel != null) channel.disconnect();
        if (session != null) session.disconnect();
    }


    @Test
    @TestId(id = "TAF_CLI_Tool_Func_033")
    public void helloWorldTest() throws Exception {
        channel.setCommand("echo \"Hello World\"\n");
        spawn = expectJ.spawn(channel);
        spawn.expect("Hello World");
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_034")
    public void wrongWayToCreateSpawnBeforeSetCommand() throws Exception {
        spawn = expectJ.spawn(channel);
        channel.setCommand("echo \"Hello World\"\n");
        try {
            spawn.expect("Hello World");
            fail();
        } catch (IOException expected) {
        }
    }

}

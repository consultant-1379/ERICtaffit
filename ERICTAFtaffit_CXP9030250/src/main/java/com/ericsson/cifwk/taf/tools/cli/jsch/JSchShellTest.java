package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLIToolException;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLITool;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import expectj.ExpectJ;
import expectj.Spawn;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Field;

public class JSchShellTest extends TafTestBase {

    Session session;
    ChannelShell channel;
    Spawn spawn;

    @BeforeMethod
    public void setUp() throws Exception {
        Host host = DataHandler.getHostByName("netsim");
        JSch jsch = new JSch();
        // jsch.setKnownHosts("/home/foo/.ssh/known_hosts");
        session = jsch.getSession(host.getUser(), host.getIp(), getPort(host));
        session.setPassword(host.getPass());
        // It must not be recommended, but if you want to skip host-key check, invoke following:
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(JSchCLITool.DEFAULT_TIMEOUT_SEC * 1000);   // making a connection with timeout millisecond.
        channel = (ChannelShell) session.openChannel("shell");
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

    @AfterMethod
    public void tearDown() throws Exception {
        try {
            if (spawn != null) spawn.stop();
        } catch (Exception ignored) {
        }
        try {
            if (channel != null) channel.disconnect();
        } catch (Exception ignored) {
        }
        try {
            if (session != null) session.disconnect();
        } catch (Exception ignored) {
        }
    }


    @Test
    @TestId(id = "TAF_CLI_Tool_Func_039")
    public void helloWorldTest() throws Exception {
        spawn = spawn(channel);
        spawn.send("echo \"Hello World\"\n");
        spawn.expect("Hello World");
    }


    Spawn spawn(Channel channel) throws IOException, NoSuchFieldException, IllegalAccessException {
        Spawn spawn = new ExpectJ(-1).spawn(channel);
        Field field = Spawn.class.getDeclaredField("m_lDefaultTimeOutSeconds");
        field.setAccessible(true);
        field.set(spawn, JSchCLITool.DEFAULT_TIMEOUT_SEC);
        // remove ECHO output into System.out
        Field fSlave = Spawn.class.getDeclaredField("slave");
        fSlave.setAccessible(true);
        Object spawnableHelper = fSlave.get(spawn);
        Field fSpawnOutToSystemOut = spawnableHelper.getClass().getDeclaredField("spawnOutToSystemOut");
        fSpawnOutToSystemOut.setAccessible(true);
        Object streamPiper = fSpawnOutToSystemOut.get(spawnableHelper);
        Field fCopyStream = streamPiper.getClass().getDeclaredField("copyStream");
        fCopyStream.setAccessible(true);
        fCopyStream.set(streamPiper, null);
        //
        return spawn;
    }

}

package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchSession;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testng.Assert.fail;

public class JSchSessionTest extends TafTestBase {

    JSchSession session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = new JSchSession("");
    }

    @AfterMethod
    public void tearDown() throws Exception {
        session.close();
    }

    static int getPort(Host host) {
        String port = host.getPort().get(Ports.SSH);
        if (port == null || port.trim().isEmpty()) return 22;
        return Integer.parseInt(port);
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_035")
    public void shouldBeConnect() throws Exception {
        Host host = DataHandler.getHostByName("netsim");
        session.open(host.getIp(), getPort(host), host.getUser(), host.getPass());
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_036")
    public void shouldThrowException_UnknownHost() throws Exception {
        String hostName = UUID.randomUUID().toString();
        Host host = DataHandler.getHostByName("netsim");
        try {
            session.open(hostName, getPort(host), host.getUser(), host.getPass());
            fail();
        } catch (JSchCLIToolException e) {
            assertThat(e.getMessage(),e.getMessage().contains("java.net.UnknownHostException") || e.getMessage().contains("timeout") || e.getMessage().contains("timed out"));
        }
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_037")
    public void shouldThrowException_UnknownPort() throws Exception {
        Host host = DataHandler.getHostByName("netsim");
        try {
            session.open(host.getIp(), 2200 + getPort(host), host.getUser(), host.getPass());
            fail();
        } catch (JSchCLIToolException e) {
            assertThat("Connection Refused Exception was not thrown", e.getMessage().toLowerCase(), containsString("connection refused"));
        } catch (Exception e){System.out.print(e);}
        
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_038")
    public void shouldThrowException_WrongPassword() throws Exception {
        Host host = DataHandler.getHostByName("training1");
        try {
            session.open(host.getIp(), getPort(host), host.getUser(), "wrong-password");
        } catch (JSchCLIToolException e) {
            assertThat(e.getMessage(), containsString("Auth fail"));
        }
    }

}

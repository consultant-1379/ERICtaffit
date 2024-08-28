package com.ericsson.cifwk.taf.tools.cli.jsch;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLIToolProvider;
import com.ericsson.cifwk.taf.tools.cli.Terminal;
import com.ericsson.nms.host.HostConfigurator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by ekonsla on 12/09/2016.
 */
public class JSchSessionReuseTest {

    private JSchCLITool jschCliTool;
    private Host ms1;

    @BeforeClass
    public void setUp() throws Exception {
        jschCliTool = (JSchCLITool) CLIToolProvider.provide();
        ms1 = HostConfigurator.getMS();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_070")
    public void sessionReuseAndCloseTest() throws Exception {
        assertThat(jschCliTool.getOpenSessionsCount(), is(0));
        for (int i = 0; i < 50; i++) {
            String shellId = jschCliTool.openShell(ms1.getIp(),
                    Integer.valueOf(ms1.getPort().get(Ports.SSH)),
                    ms1.getUser(UserType.ADMIN),
                    ms1.getPass(UserType.ADMIN), Terminal.VT100);
            // disconnects the shell only
            jschCliTool.disconnect(shellId);
        }
        // one session for all shells is reused
        assertThat(jschCliTool.getOpenSessionsCount(), is(1));
        jschCliTool.close();
        assertThat(jschCliTool.getOpenSessionsCount(), is(0));
    }
}

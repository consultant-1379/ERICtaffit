package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.cifwk.taf.taffit.test.cases.netsim.listeners.Listener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ConfigAddPortCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ConfigPortAddressCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ConfigSaveCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.commands.SelectConfigurationCommand;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class configAddNewPortTest extends TafTestBase {

    private final String portName = "TestPortAdd";
    private final String protocol = "ftp_prot";
    private final String server = "netsim";
    private final String ipAddress = "192.168.100.1";
    private final String addressPart2 = "21";

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_011")
    @DataDriven(name = "NodeData")
    public void testConfigAddPortCommands(@Input("Simulation") String simulation,
                                          @Input("NE") String nodeName) {
        if (!netsimOperator.startNe(simulation, nodeName)) {
            fail("NE " + nodeName + " was not stated, check Netsim is alive and licence is valid");
        }

        SelectConfigurationCommand selectConfigCommand = NetSimCommands.selectConfiguration();
        ConfigAddPortCommand configAddPortCommand = NetSimCommands.configAddPort(portName, protocol, server);
        ConfigPortAddressCommand configPortAddressCommand = NetSimCommands.configPortAddress(portName, ipAddress, addressPart2);
        ConfigSaveCommand configSaveCommand = NetSimCommands.configSave();
        NetSimResult resultSelect = netsimOperator.executeCommandsOnNE(simulation, nodeName, selectConfigCommand, configAddPortCommand, configPortAddressCommand, configSaveCommand);

        assertThat(resultSelect.getRawOutput()).contains(".select configuration");
        assertThat(resultSelect.getRawOutput()).contains(".config add port " + portName + " " + protocol + " " + server);
        assertThat(resultSelect.getRawOutput()).contains(".config port address " + portName + " " + ipAddress + " " + addressPart2);
        assertThat(resultSelect.getRawOutput()).contains(".config save");
        assertThat(resultSelect.getRawOutput()).doesNotContain("command unknown");
    }
}

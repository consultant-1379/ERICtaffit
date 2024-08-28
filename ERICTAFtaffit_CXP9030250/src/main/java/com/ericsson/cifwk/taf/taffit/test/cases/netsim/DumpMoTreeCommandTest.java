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
import com.ericsson.cifwk.taf.handlers.netsim.commands.DumpmotreeCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class DumpMoTreeCommandTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_012")
    @DataDriven(name = "NodeData")
    public void testDumpmotreeCommand(@Input("Simulation") String simulation,
                                      @Input("NE") String nodeName) {
        if (!netsimOperator.startNe(simulation, nodeName)) {
            fail("NE " + nodeName + " was not started, check Netsim is alive and licence is valid");
        }
        DumpmotreeCommand dumpmotreeCommand = NetSimCommands.dumpmotree();
        dumpmotreeCommand.setScope(8);
        dumpmotreeCommand.setMoid("1");
        assertThat(netsimOperator.isStarted(simulation, nodeName)).isTrue();
        NetSimResult results = netsimOperator.executeCommandsOnNE(simulation, nodeName, dumpmotreeCommand);
        assertThat(results.getRawOutput()).contains("Number of MOs");
    }
}

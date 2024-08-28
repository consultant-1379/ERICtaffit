package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;

import java.util.Map;

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
import com.ericsson.cifwk.taf.handlers.netsim.CommandOutput;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.handlers.netsim.lang.NetSimConstants;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class NeGroupTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_015")
    @DataDriven(name = "NodeData")
    public void sendCommandsToNEsFromDifferentSims(@Input("Simulation") String simulation,
                                                   @Input("NE") String nodeName) {
        NeGroup nesForSim1 = netsimOperator.getNEsFromSimulation(simulation);
        Object[] nesForSim2 = netsimOperator.getNEsFromSimulation(NetsimInformation.ALT_SIM_NAME).toArray();

        NetworkElement ne1 = nesForSim1.iterator().next();
        NetworkElement ne2 = (NetworkElement) nesForSim2[0];
        NetworkElement ne3 = (NetworkElement) nesForSim2[1];
        NeGroup collection = new NeGroup(ne1, ne2, ne3);

        Map<NetworkElement, NetSimResult> results = netsimOperator.executeCommandsOnNEGroup(simulation, collection, NetSimCommands.showFs(), NetSimCommands.start());

        verifyOutput(ne1, results.get(ne1));
        verifyOutput(ne2, results.get(ne2));
        verifyOutput(ne3, results.get(ne3));
    }

    private void verifyOutput(NetworkElement ne, NetSimResult neCommandsResult) {
        CommandOutput[] output = neCommandsResult.getOutput();
        assertThat(output.length).isGreaterThan(2);
        CommandOutput showFsOutput;
        CommandOutput showStartOutput;
        if (output[0].getRawOutput().contains(".open")) {
            showFsOutput = output[2];
            showStartOutput = output[3];
        } else {
            showFsOutput = output[1];
            showStartOutput = output[2];
        }
        assertThat(showFsOutput.getRawOutput()).contains(ne.getName() + "/fs");
        assertThat(showStartOutput.getRawOutput()).contains(NetSimConstants.COMMAND_EXEC_SUCCESS);
    }
}

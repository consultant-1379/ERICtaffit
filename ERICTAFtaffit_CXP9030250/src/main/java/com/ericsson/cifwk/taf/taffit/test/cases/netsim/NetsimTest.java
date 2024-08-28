package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static org.testng.Assert.assertTrue;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class NetsimTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @TestId(id = "TAF_NETSIM_Func_002", title = "Verify the ability of NetsimCommandHandler to list all the simulations present in Netsim")
    @Test(groups = {"Acceptance"})
    public void verifyListingAllSimulation() {
        List<Simulation> simulations = netsimOperator.getAllSimulations();
        assertThat(simulations).isNotNull();
    }

    @TestId(id = "TAF_NETSIM_Func_003", title = "Verify start and stop of a NE in a simulation ")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "NodeData")
    public void verifyStartStopOfNE(@Input("Simulation") String simulation, @Input("NE") String nE) {
        boolean isStopped = netsimOperator.stopNe(simulation, nE);
        assertThat(isStopped).isTrue();
        boolean isStarted = netsimOperator.startNe(simulation, nE);
        assertThat(isStarted).isTrue();
    }

    @TestId(id = "TAF_NETSIM_Func_004", title = "Verify the ability of NetsimCommandHandler to list all the NEs of a simulation")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "Simulations")
    public void verifyListingNEsOfSimulation(@Input("Simulation") String simulation) {
        NeGroup nEs = netsimOperator.getNEsFromSimulation(simulation);
        assertThat(nEs).isNotNull();
    }

    @TestId(id = "TAF_NETSIM_Func_005", title = "Verify execution of a command on NE")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "NodeData")
    public void verifyCommandOnNE(@Input("Simulation") String simulation, @Input("NE") String nE) {
        NetSimResult netSimResult = netsimOperator.executeCommandsOnNE(simulation, nE, NetSimCommands.start(), NetSimCommands.showalarm());
        Boolean isSuccess = true;
        CommandOutput[] commandOutputs = netSimResult.getOutput();
        for (CommandOutput commandOutput : commandOutputs) {
            if (!commandOutput.getRawOutput().contains("OK")) {
                isSuccess = false;
                break;
            }
        }
        assertThat(isSuccess).isTrue();
    }

    @TestId(id = "TAF_NETSIM_Func_006", title = "Verify execution of multiple command on NE")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "NodeData")
    public void verifyMultipleCommandOnNE(@Input("Simulation") String simulation, @Input("NE") String nE) {
        NetSimResult netSimResult = netsimOperator.executeCommandsOnNE(simulation, nE, NetSimCommands.start(), NetSimCommands.showalarm());
        Boolean isSuccess = true;
        CommandOutput[] commandOutputs = netSimResult.getOutput();
        for (CommandOutput commandOutput : commandOutputs) {
            if (!commandOutput.getRawOutput().contains("OK")) {
                isSuccess = false;
            }
            break;
        }
        assertThat(isSuccess).isTrue();
    }

    @TestId(id = "TAF_NETSIM_Func_007", title = "Verify execution of a command for all NE in a simulation")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "Simulations")
    public void verifyCommandOnSimulation(@Input("Simulation") String simulation) {
        Map<NetworkElement, NetSimResult> netSimResult = netsimOperator.executeCommandsOnSimulation(simulation, NetSimCommands.setSave());
        boolean isSuccess = true;
        for (Entry<NetworkElement, NetSimResult> entry : netSimResult.entrySet()) {
            if (!entry.getValue().toString().contains("OK")) {
                isSuccess = false;
                break;
            }
        }
        assertThat(isSuccess).isTrue();
    }

    @TestId(id = "TAF_NETSIM_Func_008", title = "Verify execution of a multiple command for all NE in a simulation")
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "Simulations2")
    public void verifyMultipleCommandOnSimulation(@Input("Simulation") String simulation, @Input("NEList") String neList) {
        int startedOriginalSize = netsimOperator.getSimulationsStartedNEs(simulation).size();
        assertThat(startedOriginalSize).isEqualTo(0);
        try {
            Map<NetworkElement, NetSimResult> netSimResult = netsimOperator.executeCommandsOnSimulation(simulation, NetSimCommands.setSave(), NetSimCommands.start());
            boolean isSuccess = true;
            for (Entry<NetworkElement, NetSimResult> entry : netSimResult.entrySet()) {
                if (!entry.getValue().toString().contains("OK")) {
                    isSuccess = false;
                    break;
                }
            }
            assertThat(netsimOperator.getSimulationsStartedNEs(simulation)).hasSize(2);
            assertTrue(isSuccess);
        } finally {
            NeGroup nesToStart = new NeGroup();
            for (String ne : neList.split(";")) {
                nesToStart.add(netsimOperator.getSingleNEsFromSimulation(simulation, ne));
            }
            netsimOperator.executeCommandsOnNEGroup(simulation, nesToStart, NetSimCommands.stop());
            assertThat(netsimOperator.getSimulationsStartedNEs(simulation)).hasSize(startedOriginalSize);
        }
    }
}

package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

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
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class SimulationTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @DataDriven(name = "NodeData")
    public void exec(@Input("Simulation") String simulation,
                     @Input("NE") String nodeName) {
        NetSimResult result = netsimOperator.executeCommandsWithSession(NetSimCommands.open(simulation), NetSimCommands.showSimnes());
        CommandOutput[] output = result.getOutput();
        assertThat(output).hasLength(2);
        assertThat(output[1].getRawOutput()).contains(nodeName);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_033")
    public void getAllNEs() {
        List<NetworkElement> allNEs = netsimOperator.getAllNEs();
        assertThat(allNEs).hasSize(NetsimInformation.TOTAL_NO_OF_NES);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_034")
    @DataDriven(name = "NodeData")
    public void getNetworkElement(@Input("Simulation") String simulation,
                                  @Input("NE") String nodeName) {
        String neName = nodeName;
        NetworkElement networkElement = netsimOperator.getSingleNEsFromSimulation(simulation, neName);
        assertThat(neName).isEqualTo(networkElement.getName());
        assertThat(netsimOperator.getSingleNEsFromSimulation(simulation, "idontexist")).isNull();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_042")
    public void startAndStopAllNEsInSimulation(){
        netsimOperator = provider.get();
        List<NetworkElement> simulationStartedNEs = netsimOperator.getSimulationsStartedNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL);

        assertThat(netsimOperator.stopAllNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL)).isTrue();

        assertThat(netsimOperator.getSimulationsStartedNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL).size()).isEqualTo(0);

        assertThat(netsimOperator.startAllNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL)).isTrue();

        int currentStartedCount = netsimOperator.getSimulationsStartedNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL).size();
        assertThat(currentStartedCount).isAtLeast(simulationStartedNEs.size());

        if(currentStartedCount > simulationStartedNEs.size()) {
            assertThat(netsimOperator.stopAllNEs(NetsimInformation.SIMULATION_FOR_START_STOP_ALL)).isTrue();
            // Start them in parallel again?
            for (final NetworkElement ne : simulationStartedNEs) {
                netsimOperator.executeCommandsOnNE(NetsimInformation.SIMULATION_FOR_START_STOP_ALL, ne.getName(), NetSimCommands.start());
            }
        }
    }
}

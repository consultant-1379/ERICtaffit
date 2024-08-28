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
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class NetSimCommandHandlerTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_016")
    public void getAllSimulations() {
        List<Simulation> sims = netsimOperator.getAllSimulations();
        assertThat(sims).hasSize(NetsimInformation.TOTAL_NO_OF_OPEN_SIMULATIONS);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_017")
    @DataDriven(name = "NodeData")
    public void getSimulations(@Input("Simulation") String simulation) {
        SimulationGroup simulations = netsimOperator.getSimulations(simulation,
                "idontexist", "default");
        assertThat(simulations).hasSize(2);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_018")
    @DataDriven(name = "Simulations")
    public void getSimulationNEs(@Input("Simulation") String simulation, @Input("NEcount") Integer neCount) {
        NeGroup simulationNEs = netsimOperator.getNEsFromSimulation(simulation,
                "idontexist", "default");
        assertThat(simulationNEs).hasSize(neCount);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_019")
    @DataDriven(name = "NodeData3")
    public void isStarted(@Input("Simulation") String simulation,
                          @Input("NE") String nodeName) {
        boolean isStarted = netsimOperator.isStarted(simulation, nodeName);
        int extraNode = 1;
        if (isStarted) {
            extraNode = 0;
        }
        try {
            netsimOperator.executeCommandsWithSession(NetSimCommands.open(simulation),
                    NetSimCommands.selectnocallback(nodeName),
                    NetSimCommands.stop());

            boolean isStartedBeforeCommand = netsimOperator.isStarted(simulation, nodeName);
            boolean iDontExistStarted = netsimOperator.isStarted(simulation, "idnotexist");

            netsimOperator.startNe(simulation, nodeName);

            assertThat(isStartedBeforeCommand).isFalse();
            assertThat(iDontExistStarted).isFalse();

            assertThat(netsimOperator.isStarted(simulation, nodeName)).isTrue();
            assertThat(netsimOperator.getAllStartedNEs()).hasSize(NetsimInformation.TOTAL_NO_OF_STARTED_NES + extraNode);
        } finally {
            if (!isStarted) {
                netsimOperator.stopNe(simulation, nodeName);
            }
            netsimOperator.closeSession();
        }
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_020")
    @DataDriven(name = "NodeData3")
    public void getAllStartedNEs(@Input("Simulation") String simulation,
                                 @Input("NE") String nodeName) {
        NeGroup allStartedNes;
        boolean stopNodeAfterTest = false;
        if (netsimOperator.isStarted(simulation, nodeName)) {
            allStartedNes = netsimOperator.getAllStartedNEs();
            assertThat(allStartedNes.size()).isEqualTo(NetsimInformation.TOTAL_NO_OF_STARTED_NES);
        } else {
            stopNodeAfterTest = true;
            netsimOperator.startNe(simulation, nodeName);
            allStartedNes = netsimOperator.getAllStartedNEs();
            assertThat(allStartedNes.size()).isEqualTo(NetsimInformation.TOTAL_NO_OF_STARTED_NES + 1);
        }
        NetworkElement neToTest = netsimOperator.getSingleNEsFromSimulation(simulation, nodeName);

        for (final NetworkElement ne : allStartedNes) {
            if (ne.getName().equals(nodeName)) {
                assertThat(ne.isStarted()).isTrue();
                assertThat(ne.getName()).isEqualTo(neToTest.getName());
                assertThat(ne.getType()).isEqualTo(neToTest.getType());
                assertThat(ne.getNodeType()).isEqualTo(neToTest.getNodeType());
                assertThat(ne.getTechType()).isEqualTo(neToTest.getTechType());
                assertThat(ne.getHostName()).isEqualTo(neToTest.getHostName());
                assertThat(ne.getSimulationName()).isEqualTo(neToTest.getSimulationName());
                break;
            }
        }
        if (stopNodeAfterTest) {
            netsimOperator.stopNe(simulation, nodeName);
        }
    }
}

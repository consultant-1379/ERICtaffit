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
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class SimulationGroupTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_031")
    @DataDriven(name = "SimulationsList")
    public void getSimulations(@Input("Simulations") String rawSimulations,
                               @Input("NodeCount") String rawNodeCount,
                               @Input("TotalNodes") Integer totalNodeCount) {
        String[] split = rawSimulations.split(";");
        SimulationGroup sims = getSimsForNames(split[0], split[1], "idontexist", "idontexistToo");
        assertThat(sims).hasSize(2);
        assertThat(sims.getAllNEs()).hasSize(totalNodeCount);

        Map<String, Simulation> simulations = sims.getSimulationsMap();
        Simulation sim = simulations.get(split[0]);
        assertThat(sim.getAllNEs()).hasSize(Integer.parseInt(rawNodeCount.split(";")[0]));
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_032")
    @DataDriven(name = "SimulationsList")
    public void addAndRemoveSimulations(@Input("Simulations") String simulationsList, @Input("NodeCount") String nodeCount) {
        String[] sims = simulationsList.split(";");
        String[] simsNE = nodeCount.split(";");
        SimulationGroup hostSims = getSimsForNames(sims);
        assertThat(hostSims).hasSize(sims.length);

        Map<String, Simulation> simulations = hostSims.getSimulationsMap();

        SimulationGroup newCollection = new SimulationGroup(simulations.get(sims[1]));
        assertThat(newCollection).hasSize(1);
        assertThat(newCollection.getAllNEs()).hasSize(Integer.parseInt(simsNE[1]));

        newCollection.add(simulations.get(sims[0]));
        assertThat(newCollection).hasSize(2);
        assertThat(newCollection.getAllNEs()).hasSize(Integer.parseInt(simsNE[0]) + Integer.parseInt(simsNE[1]));

        newCollection.add(simulations.get(sims[0]));
        assertThat(newCollection).hasSize(3);
        assertThat(newCollection.getAllNEs()).hasSize(Integer.parseInt(simsNE[0]) * 2 + Integer.parseInt(simsNE[1]));

        newCollection.remove(simulations.get(sims[0]));
        newCollection.remove(simulations.get(sims[1]));
        assertThat(newCollection).hasSize(1);
        assertThat(newCollection.getAllNEs()).hasSize(Integer.parseInt(simsNE[0]));
    }

    private SimulationGroup getSimsForNames(String... simNames) {
        return netsimOperator.getSimulations(simNames);
    }
}

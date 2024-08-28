package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.cifwk.taf.taffit.test.cases.netsim.listeners.Listener;
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
public class ResultProcessorTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;

    @Test
    @TestId(id = "TAF_NETSIM_Func_024")
    @DataDriven(name = "NodeData")
    public void checkCommands(@Input("Simulation") String simulation) {
        NetsimOperatorImpl netsimOperator = provider.get();
        Map<NetworkElement, NetSimResult> result = netsimOperator.executeCommandsOnSimulation(simulation, NetSimCommands.showStarted());

        Entry<NetworkElement, NetSimResult> entry = result.entrySet().iterator().next();
        int outputLength = entry.getValue().getOutput().length;
        CommandOutput commandOutput = entry.getValue().getOutput()[outputLength-1];
        Map<String, List<Map<String, String>>> sections = commandOutput.asSections();
        if (sections.size() > 0) {
            Collection<List<Map<String, String>>> sectionsList = sections.values();
            for (List<Map<String, String>> section : sectionsList) {
                checkSectionRows(section);
            }
        } else {
            fail("No result returned for commands executed on sim " + simulation);
        }
    }

    private void checkSectionRows(List<Map<String, String>> section) {
        for (Map<String, String> rowMap : section) {
            assertThat(rowMap.get("NE")).isNotSameAs("");
            assertThat(rowMap.get("Address")).isNotSameAs("");
            assertThat(rowMap.get("Simulation/Commands")).isNotSameAs("");
        }
    }
}

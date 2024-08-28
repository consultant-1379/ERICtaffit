package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.AvcburstCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class AvcburstCommandTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    private AvcburstCommand avcburstCommand;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_010")
    @DataDriven(name = "NodeData2")
    public void testAvcburstcommand(@Input("Simulation") String simulation,
                                    @Input("NE1") String nodeName1,
                                    @Input("NE2") String nodeName2,
                                    @Input("NE3") String nodeName3) {
        NeGroup neGroup = new NeGroup();
        neGroup.add(netsimOperator.getSingleNEsFromSimulation(simulation, nodeName1));
        neGroup.add(netsimOperator.getSingleNEsFromSimulation(simulation, nodeName2));

        NetworkElement ne = netsimOperator.getSingleNEsFromSimulation(simulation, nodeName3);
        neGroup.add(ne);

        buildAvcBurstCommand();

        NetSimResult result = netsimOperator.executeCommandsOnNEGroup(simulation, neGroup, avcburstCommand).get(ne);

        assertThat(result.getOutput()).hasLength(2);
        assertThat(result.getOutput()[0].toString()).contains(">> .selectnocallback");
        assertThat(result.getOutput()[1].toString()).contains(">> avcburst");
    }

    private void buildAvcBurstCommand() {
        avcburstCommand = NetSimCommands.avcburst();
        avcburstCommand.setFreq(new BigDecimal(2));
        Map<String, String> values = new HashMap<String, String>();
        values.put("userLabel", "one");
        values.put("userLabel", "two");

        Map<String, Map> data = new HashMap<String, Map>();
        data.put("ManagedElement=1,ENodeBFunction=1", values);
        List<Map> avcData = new ArrayList<Map>();
        avcData.add(data);

        avcburstCommand.setAvcdata(avcData);
        avcburstCommand.setLoop(false);
        avcburstCommand.setNumEvents(2);
        avcburstCommand.setIdleTime(0l);
        avcburstCommand.setMode(AvcburstCommand.Mode.TEMP);
    }
}

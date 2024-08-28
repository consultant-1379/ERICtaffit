package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
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
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimException;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.commands.OpenCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ShowSimnesCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ShowSimulationsCommand;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class SshNetSimSessionTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_037")
    @DataDriven(name = "NodeData")
    public void execListOfNetsimCommands(@Input("Simulation") String simulation) {
        ShowSimulationsCommand showSimulationsCmd = NetSimCommands.showSimulations();
        OpenCommand openSim1Cmd = NetSimCommands.open(simulation);
        ShowSimnesCommand showSimnesCmd = NetSimCommands.showSimnes();

        NetSimResult result = netsimOperator.executeCommandsWithSession(showSimulationsCmd);
        CommandOutput[] output = result.getOutput();
        assertThat(output).hasLength(1);
        assertThat(output[0].getRawOutput()).contains(".show simulations");

        result = netsimOperator.executeCommandsWithSession(openSim1Cmd, showSimnesCmd);
        output = result.getOutput();
        assertThat(output).hasLength(2);
        assertThat(output[0].getRawOutput()).contains(simulation);
        assertThat(output[1].getRawOutput()).contains(".show simnes");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_038")
    @DataDriven(name = "NodeData")
    public void executeNetsimSessionCommandWithTimoutUsingArray(@Input("Simulation") String simulation, @Input("NE") String nodeName) {
        int scope = 3;
        String command = nodeName + "\ndumpmotree:moid=\"ManagedElement=1\",scope=" + scope + ",printattrs;";
        NetSimResult dumpMoResults = null;
        CommandOutput[] cos = null;
        do {
            dumpMoResults = netsimOperator.executeCommandsWithSession(120, NetSimCommands.open(simulation), NetSimCommands.selectnocallback(command));
            cos = dumpMoResults.getOutput();
        } while (cos.length <= 1);
        CommandOutput co = cos[2];
        String rawOut = co.getRawOutput();
        assertThat(rawOut).contains("ManagedElement=");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_039")
    @DataDriven(name = "NodeData")
    public void executeNetsimSessionCommandWithTimoutUsingList(@Input("Simulation") String simulation, @Input("NE") String nodeName) {
        int scope = 3;
        String command = nodeName + "\ndumpmotree:moid=\"ManagedElement=1\",scope=" + scope + ",printattrs;";
        List<NetSimCommand> commandList = new ArrayList<NetSimCommand>();
        NetSimCommand openCommand = NetSimCommands.open(simulation);
        NetSimCommand selectNoCallBack = NetSimCommands.selectnocallback(command);
        commandList.add(openCommand);
        commandList.add(selectNoCallBack);
        NetSimResult dumpMoResults = null;
        CommandOutput[] cos = null;
        do {
            dumpMoResults = netsimOperator.executeCommandsWithSession(120, commandList);
            cos = dumpMoResults.getOutput();
        } while (cos.length <= 1);
        CommandOutput co = cos[2];
        String rawOut = co.getRawOutput();
        assertThat(rawOut).contains("ManagedElement=");
    }

    @Test(expectedExceptions = NetSimException.class)
    @TestId(id = "TAF_NETSIM_Func_040")
    @DataDriven(name = "NodeData")
    public void shouldThrowCommandTimeoutException(@Input("Simulation") String simulation,
                                                   @Input("NE") String nodeName) {
        int scope = 6;
        String command = nodeName + "\ndumpmotree:moid=\"ManagedElement=1\",scope=" + scope + ",printattrs;";
        NetSimResult dumpMoResults = null;
        CommandOutput[] cos = null;
        netsimOperator.startNe(simulation, nodeName);
        do {
            dumpMoResults = netsimOperator.executeCommandsWithSession(1, NetSimCommands.open(simulation), NetSimCommands.selectnocallback(command));
            cos = dumpMoResults.getOutput();
        } while (cos.length <= 1);
    }
}

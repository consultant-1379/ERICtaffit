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
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.handlers.netsim.CommandOutput;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.commands.OpenCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ShowSimnesCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.ShowStartedCommand;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class ShowSimulationsCommandTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_025")
    public void shouldShowSimulations() {
        CommandOutput[] output = exec(NetSimCommands.showSimulations());
        assertThat(output[0].asList()).hasSize(NetsimInformation.TOTAL_NO_OF_OPEN_SIMULATIONS);
        assertThat(output[0].asList()).contains("default");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_026")
    public void shouldShowSimnes_NotFound() {
        CommandOutput[] output = exec(NetSimCommands.showSimnes());
        assertThat(output[0].asList().size()).isEqualTo(1);
        assertThat(output[0].asList().get(0)).isEqualTo("There are no simnes");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_027")
    public void shouldOpen() {
        CommandOutput[] output = exec(NetSimCommands.showSimulations());
        String simulation = output[0].asList().get(6);

        OpenCommand command = NetSimCommands.open();
        command.setSimulation(simulation);
        CommandOutput[] output2 = exec(command);
        assertThat(output2[0].asList().isEmpty()).isTrue();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_029")
    public void shouldPrintAllNes() {
        CommandOutput[] output = exec(NetSimCommands.showSimulations());
        List<String> simulations = output[0].asList();

        List<NetSimCommand> commands = new ArrayList<>();
        for (String simulation : simulations) {
            OpenCommand openCommand = NetSimCommands.open();
            openCommand.setSimulation(simulation);
            commands.add(openCommand);

            ShowSimnesCommand showSimnesCommand = NetSimCommands.showSimnes();
            commands.add(showSimnesCommand);
        }
        CommandOutput[] neOutput = exec(commands.toArray(new NetSimCommand[commands.size()]));

        assertThat(neOutput.length).isEqualTo(NetsimInformation.TOTAL_NO_OF_OPEN_SIMULATIONS * 2);
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_030")
    public void shouldShowStarted() {
        ShowStartedCommand command = NetSimCommands.showStarted();
        CommandOutput[] outputs = exec(command);
        assertThat(outputs).hasLength(1);
        assertThat(outputs[0].toString()).contains("NE");
        assertThat(outputs[0].toString()).contains("Address");
        assertThat(outputs[0].toString()).contains("Simulation/Commands");
    }

    private CommandOutput[] exec(NetSimCommand... commands) {
        NetSimResult result = netsimOperator.executeCommandsWithContext(commands);
        return result.getOutput();
    }
}

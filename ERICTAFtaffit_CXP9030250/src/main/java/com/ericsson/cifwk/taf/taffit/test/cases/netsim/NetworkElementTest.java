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
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.netsim.CommandOutput;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommandHandler;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;
import com.ericsson.nms.host.HostConfigurator;

@Listeners(Listener.class)
public class NetworkElementTest extends TafTestBase {

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_022")
    @DataDriven(name = "NodeData3")
    public void exec(@Input("Simulation") String simulation,
                     @Input("NE") String nodeName) {
        NetSimResult result = netsimOperator.executeCommandsOnNE(simulation, nodeName, NetSimCommands.showFs());
        CommandOutput[] output = result.getOutput();
        assertThat(output.length).isEqualTo(1);
        assertThat(output[0].getRawOutput()).contains(nodeName + "/fs");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_023")
    @DataDriven(name = "NodeData3")
    public void isStarted(@Input("Simulation") String simulation,
                          @Input("NE") String nodeName) {
        assertThat(netsimOperator.stopNe(simulation, nodeName)).isTrue();
        assertThat(netsimOperator.isStarted(simulation, nodeName)).isFalse();
        assertThat(netsimOperator.startNe(simulation, nodeName)).isTrue();
        assertThat(netsimOperator.isStarted(simulation, nodeName)).isTrue();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_041")
    public void getHost() {
        List<Host> hosts = HostConfigurator.getAllNetsimHosts();
        Host host = hosts.get((int) (Math.random() * hosts.size()));
        NetSimCommandHandler handler = NetSimCommandHandler.getInstance(host);
        List<NetworkElement> nes = handler.getAllSimulations().getAllNEs();
        NetworkElement ne = nes.get((int) (Math.random() * nes.size()));
        assertThat(ne.getHost().getIp()).contains(host.getIp());
    }
}

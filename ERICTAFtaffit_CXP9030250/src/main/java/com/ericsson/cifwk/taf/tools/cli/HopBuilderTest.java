package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.taffit.test.cli.flows.HopBuilderFlows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;

public class HopBuilderTest extends TafTestBase {

    @Inject
    private HopBuilderFlows hopBuilderFlows;
    private final static String PATH_TO_KEY_FILE = "/root/.ssh/vm_private_key";
    private final static String CLOUD_USER = "cloud-user";

    @BeforeClass
    public void setup() {
        TestScenario scenario = scenario("Hopbuilder setup")
                .addFlow(hopBuilderFlows.removePmservHostKey())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestSuite("Hopping_On_Current_Host")
    public void shouldHopToNewUserOnCurrentHost(){
        TestScenario scenario = dataDrivenScenario("Hop to new user on current host")
                .addFlow(hopBuilderFlows.hopToNewUserOnCurrentHost())
                .withScenarioDataSources(dataSource("hopOnSameHost"),dataSource("user"))
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestSuite("Hopping_To_New_Host_With_Default_User")
    public void shouldHopToNewHostWithDefaultUser(){
        TestScenario scenario = dataDrivenScenario("Hop to new host with default user")
                .addFlow(hopBuilderFlows.hopToNewHostWithDefaultUser())
                .withScenarioDataSources(dataSource("hopToNewHostDefaultUser"))
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestSuite("Hopping_To_New_Host_With_New_User")
    public void shouldHopToNewHostWithNewUser(){
        TestScenario scenario = dataDrivenScenario("Hop to new host with new user")
                .addFlow(hopBuilderFlows.hopToNewHostWithNewUser())
                .withScenarioDataSources(dataSource("hopToNewHostWithNewUser"))
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestSuite("Hop to host in /etc/hosts file that is not in host properties")
    public void shouldHopToHostInEtcHostsFile() {
        TestScenario scenario = dataDrivenScenario("Hop to new host from /etc/hosts file")
                .addFlow(hopBuilderFlows.hopToHostFromEtcHosts())
                .withScenarioDataSources(dataSource("hopToHostFromEtcHostsFile"))
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_011")
    public void shouldSupportMultipleHops() {
        TestScenario scenario = scenario("Multiple hops")
                .addFlow(hopBuilderFlows.multipleHops())
                .build();
        executeScenario(scenario);
    }

    @TestId(id="TAF_HopBuilder_Func_009")
    @Test
    public void shouldHopToIPv6EnabledHost() {
        TestScenario scenario = scenario("Hop to new host with IPv6 address")
                .addFlow(hopBuilderFlows.hopToNewHostWithIPv6Address())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_015")
    public void hopToServerWithHostAndFileOnly(){
        TestScenario scenario = scenario("Hop to VM specifying host and keyfile")
                .addFlow(hopBuilderFlows.hopToVmWithHostAndFile())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_016")
    public void hopToServerWithHostUserAndFile(){
        TestScenario scenario = scenario("Hop to VM specifying host, user and keyfile")
                .addFlow(hopBuilderFlows.hopToVmWithHostUserAndFile())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_017")
    public void hopToServerWithHostStrictAuthAndFile(){
        TestScenario scenario = scenario("Hop to VM specifying host, strict authentication and keyfile")
                .addFlow(hopBuilderFlows.hopToVmWithHostStrictAuthAndFile())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_018")
    public void hopToServerWithHostUserStrictAuthAndFile(){
        TestScenario scenario = scenario("Hop to VM specifying host, user, strict authentication and keyfile")
                .addFlow(hopBuilderFlows.hopToVmWithHostUserStrictAuthAndFile())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_019")
    public void verifyHopFromSvcToVmThrowsFailure(){
        TestScenario scenario = scenario("Attempt to hop from SVC to service VM")
                .addFlow(hopBuilderFlows.hopToVmHostFromSvcWithFile())
                .build();
        executeScenario(scenario);
    }

    @Test
    @TestId(id="TAF_HopBuilder_Func_020")
    public void verifyFailureHoppingAsCloudUserFromMSToSVC(){
        TestScenario scenario = scenario("Try hopping from MS to SVC as cloud-user")
                .addFlow(hopBuilderFlows.hopToSvcFromMsWithFile())
                .build();
        executeScenario(scenario);
    }

    private void executeScenario(final TestScenario scenario) {
        TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
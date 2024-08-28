package com.ericsson.cifwk.taf.taffit.test.cli.flows;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.taffit.test.cli.teststeps.HopBuilderTestSteps;

import javax.inject.Inject;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;

public class HopBuilderFlows {

    @Inject
    private HopBuilderTestSteps hopBuilderTestSteps;

    public TestStepFlow hopToNewUserOnCurrentHost(){
        return flow("Hop to new user on current host")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .build();
    }

    public TestStepFlow hopToNewHostWithDefaultUser() {
        return flow("Hop to new host with default user")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_HOST_DEFAULT_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .build();
    }

    public TestStepFlow hopToNewHostWithNewUser() {
        return flow("Hop to new host with new user")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_HOST_NEW_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .build();
    }

    public TestStepFlow hopToHostFromEtcHosts() {
        return flow("Hop to new host from /etc/hosts file")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_HOST_FROM_ETC_HOSTS_FILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .build();
    }

    public TestStepFlow multipleHops() {
        return flow("Multiple hops")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_HOST_DEFAULT_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("multipleHops"), dataSource("multipleHopsUser").bindTo("user"))
                .build();
    }

    public TestStepFlow hopToNewHostWithIPv6Address() {
        return flow("Hop to new host with IPv6 Address")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_NEW_HOST_WITH_IPv6_ADDRESS))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hopToNewHostWithIPv6Address"))
                .build();
    }

    //HopWithKeyFile specific flows
    public TestStepFlow hopToVmWithHostAndFile() {
        return flow("Hop to Service VM with host and keyfile specified")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_VM_HOST_AND_FILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hostWithKeyFileData"))
                .build();
    }

    public TestStepFlow hopToVmWithHostUserAndFile() {
        return flow("Hop to Service VM with host, user and keyfile specified")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_VM_HOST_USER_AND_FILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hostWithKeyFileData"))
                .build();
    }


    public TestStepFlow hopToVmWithHostStrictAuthAndFile() {
        return flow("Hop to Service VM with host, strict authentication and keyfile specified")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_VM_HOST_STRICTAUTH_AND_FILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hostWithKeyFileData"))
                .build();
    }

    public TestStepFlow hopToVmWithHostUserStrictAuthAndFile() {
        return flow("Hop to Service VM with host, user, strict authentication and keyfile specified")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_VM_HOST_USER_STRICTAUTH_AND_FILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hostWithKeyFileData"))
                .build();
    }

    public TestStepFlow hopToVmHostFromSvcWithFile() {
        return flow("Hop to Service VM from SVC with keyfile specified")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_VM_HOST_WITH_NO_KEYFILE))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hopWithSvcHost"))
                .build();
    }

    public TestStepFlow hopToSvcFromMsWithFile() {
        return flow("Hop to SVC from MS with key file")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.HOP_TO_SVC_FROM_MS_AS_CLOUD_USER))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hopWithMsAndSvcData"))
                .build();
    }

    public TestStepFlow removePmservHostKey() {
        return flow("Removing pmserv_1 host key file")
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGIN))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.REMOVE_PMSERV_HOSTKEY))
                .addTestStep(annotatedMethod(hopBuilderTestSteps, HopBuilderTestSteps.LOGOUT))
                .withDataSources(dataSource("hostWithKeyFileData"))
                .build();
    }

}
package com.ericsson.cifwk.taf.taffit.test.cli.teststeps;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelperConstants;
import com.ericsson.enm.UserSession;

import javax.inject.Inject;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

public class HopBuilderTestSteps {

    public static final String LOGIN = "login";
    public static final String HOP_TO_NEW_USER = "hopToNewUser";
    public static final String LOGOUT = "logout";
    public static final String HOP_TO_NEW_HOST_DEFAULT_USER = "hopToNewHostWithDefaultUser";
    public static final String HOP_TO_NEW_HOST_NEW_USER = "hopToNewHostWithNewUser";
    public static final String HOP_TO_HOST_FROM_ETC_HOSTS_FILE = "hopToHostFromEtcHostsFile";
    public static final String HOP_TO_NEW_HOST_WITH_IPv6_ADDRESS = "hopToNewHostWithIPv6Address";
    public static final String HOP_TO_VM_HOST_AND_FILE = "hopToVMWithHostAndFile";
    public static final String HOP_TO_VM_HOST_USER_AND_FILE = "hopToVMWithHostUserAndFile";
    public static final String HOP_TO_VM_HOST_STRICTAUTH_AND_FILE = "hopToVMWithHostStrictAuthAndFile";
    public static final String HOP_TO_VM_HOST_USER_STRICTAUTH_AND_FILE = "hopToVMWithHostUserStrictAuthAndFile";
    public static final String HOP_TO_VM_HOST_WITH_NO_KEYFILE = "hopToVmHostWithNoKeyFile";
    public static final String HOP_TO_SVC_FROM_MS_AS_CLOUD_USER = "hopToSvcFromMsAsCloudUser";
    public static final String REMOVE_PMSERV_HOSTKEY = "removePmservHostKey";

    @Inject
    private CLICommandHelper cliCommandHelper;

    @Inject
    private TestContext testContext;

    @TestStep(id = LOGIN)
    public void login(@Input("hostname") String hostname, @Input("loginUsername") String username, @Output("expectedOriginalUsername") String expectedOriginalUsername) {
        final Host host = DataHandler.getHostByName(hostname);
        cliCommandHelper.createCliInstance(host, host.getUser(username));
        verifyUser(expectedOriginalUsername);
        UserSession session = new UserSession();
        session.setTool(cliCommandHelper);
        testContext.setAttribute(UserSession.SESSION, session);
    }

    private void verifyUser(String expectedUsername) {
        String stdOut = cliCommandHelper.execute("whoami");
        assertThat(stdOut).contains(expectedUsername);
    }

    @TestStep(id = HOP_TO_NEW_USER)
    public void hopToNewUser(@Input("user") User newUser, @Output("expectedUser") String expectedUser) {
        try {
            getCliCommandHelper().newHopBuilder().hop(new User(newUser.getUsername(),newUser.getPassword(), UserType.ADMIN)).build();
        } catch (RuntimeException e){
            assertThat(e.getMessage()).contains(newUser.getUsername());
        }
        verifyUser(expectedUser);
    }

    @TestStep(id = HOP_TO_NEW_HOST_DEFAULT_USER)
    public void hopToNewHostWithDefaultUser(@Input("newHostname") String newHostname, @Input("strictHostChecking") boolean strictHostChecking, @Output("expectedNewHostIP")
    String expectedNewHostIP, @Output("expectedUsername") final String expectedUsername, @Output("expectedExceptionMessage") final String expectedExceptionMessage) {
        final Host newHost = DataHandler.getHostByName(newHostname);
        try {
            getCliCommandHelper().newHopBuilder().hop(newHost, strictHostChecking).build();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains(expectedExceptionMessage);
        }
        verifyUser(expectedUsername);
        verifyIP(expectedNewHostIP);
    }

    private void verifyIP(final @Output("expectedNewHostIP") String expectedNewHostIP) {
        String stdOut = getCliCommandHelper().execute("ifconfig");
        assertThat(stdOut).contains(expectedNewHostIP);
    }

    @TestStep(id = HOP_TO_NEW_HOST_NEW_USER)
    public void setHopToNewHostNewUser(@Input("newHostname") final String newHostname, @Input("newUsername") final String newUsername, @Input("newPassword")
    final String newPassword, @Input("strictHostChecking") final boolean strictHostChecking, @Output("expectedHostIP") final String expectedHostIP, @Output("expectedUsername")
                                       final String expectedUsername, @Output("expectedExceptionMessage") final String expectedExceptionMessage) {
        final Host newHost = DataHandler.getHostByName(newHostname);
        final User newUser = new User(newUsername, newPassword, UserType.ADMIN);
        try {
            getCliCommandHelper().newHopBuilder().hop(newHost, newUser, strictHostChecking).build();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains(expectedExceptionMessage);
        }
        verifyIP(expectedHostIP);
        verifyUser(expectedUsername);
    }

    @TestStep(id = HOP_TO_HOST_FROM_ETC_HOSTS_FILE)
    public void hopToHostFromEtcHostsFile(@Input("newHostname") String newHostname, @Input("strictHostChecking") boolean strictHostChecking,
                                          @Input("newUsername") String newUsername, @Input("newPassword") String newPassword,
                                          @Output("expectedNewHostIP") String expectedNewHostIP) {
        Host newHost = Host.builder().withName(newHostname).build();
        User newUser = new User(newUsername, newPassword, UserType.ADMIN);
        try {
            getCliCommandHelper().newHopBuilder().hop(newHost, newUser, strictHostChecking).build();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("No IP was supplied for the destination host and failed to retrieve IP from /etc/hosts");
            return;
        }
        verifyIP(expectedNewHostIP);
        verifyUser(newUsername);
    }

    @TestStep(id=HOP_TO_NEW_HOST_WITH_IPv6_ADDRESS)
    public void hopToNewHostWithIPv6Address(@Input("newHostname") final String newHostname, @Input("strictHostChecking")final boolean strictHostChecking){
        final Host newHost = DataHandler.getHostByName(newHostname);
        final String ipv6Address = DataHandler.getAttribute("host.svc1.ipv6").toString();
        newHost.setIp(ipv6Address);
        getCliCommandHelper().newHopBuilder().hop(newHost, strictHostChecking).build();
        verifyUser(newHost.getUser());
        verifyIP(ipv6Address);
    }

    private CLICommandHelper getCliCommandHelper() {
        UserSession session = testContext.getAttribute(UserSession.SESSION);
        return session.getTool().getAs(CLICommandHelper.class);
    }

    @TestStep(id = LOGOUT)
    public void logout() {
        getCliCommandHelper().disconnect();
        assertThat(getCliCommandHelper().isClosed()).isTrue();
    }

    @TestStep(id=HOP_TO_VM_HOST_AND_FILE)
    public void hopToVMWithHostAndFile(@Input("vmHostname") String vmHostname, @Input("pathToKeyFile") String pathToKeyFile,
                                       @Input("cloudUser") String cloudUser){
        final Host serviceVM = DataHandler.getHostByName(vmHostname);
        getCliCommandHelper().newHopBuilder().hopWithKeyFile(serviceVM, pathToKeyFile).build();
        verifyHopWithKeyFile(cloudUser, serviceVM);
    }

    @TestStep(id=HOP_TO_VM_HOST_USER_AND_FILE)
    public void hopToVMWithHostUserAndFile(@Input("vmHostname") String vmHostname, @Input("pathToKeyFile") String pathToKeyFile,
                                           @Input("cloudUser") String cloudUser, @Input("cloudUserPassword") String cloudUserPassword,
                                           @Input("userType") UserType userType){
        final Host serviceVM = DataHandler.getHostByName(vmHostname);
        final User userCredentials = new User(cloudUser, cloudUserPassword, userType);
        getCliCommandHelper().newHopBuilder().hopWithKeyFile(serviceVM, userCredentials, pathToKeyFile).build();
        verifyHopWithKeyFile(cloudUser, serviceVM);
    }

    @TestStep(id = HOP_TO_VM_HOST_STRICTAUTH_AND_FILE)
    public void hopToVMWithHostStrictAuthAndFile(@Input("vmHostname") String vmHostname, @Input("pathToKeyFile") String pathToKeyFile,
                                                 @Input("cloudUser") String cloudUser, @Input("strictAuthentication") boolean strictAuthentication){
        final Host serviceVM = DataHandler.getHostByName(vmHostname);
        getCliCommandHelper().newHopBuilder().hopWithKeyFile(serviceVM, strictAuthentication, pathToKeyFile).build();
        verifyHopWithKeyFile(cloudUser, serviceVM);
    }

    @TestStep(id = HOP_TO_VM_HOST_USER_STRICTAUTH_AND_FILE)
    public void hopToVMWithHostUserStrictAuthAndFile(@Input("vmHostname") String vmHostname, @Input("pathToKeyFile") String pathToKeyFile,
                                                     @Input("cloudUser") String cloudUser, @Input("cloudUserPassword") String cloudUserPassword,
                                                     @Input("userType") UserType userType, @Input("strictAuthentication") boolean strictAuthentication){
        final Host serviceVM = DataHandler.getHostByName(vmHostname);
        final User userCredentials = new User(cloudUser, cloudUserPassword, userType);
        getCliCommandHelper().newHopBuilder().hopWithKeyFile(serviceVM, userCredentials, strictAuthentication, pathToKeyFile).build();
        verifyHopWithKeyFile(cloudUser, serviceVM);
    }

    @TestStep(id = HOP_TO_VM_HOST_WITH_NO_KEYFILE)
    public void hopToVmHostWithNoKeyFile(@Input("hostname") String hostname, @Input("vmHostname") String vmHostname,
                                         @Input("pathToKeyFile") String pathToKeyFile){
        final Host initialHost = DataHandler.getHostByName(hostname);
        final Host serviceVM = DataHandler.getHostByName(vmHostname);
        try {
            getCliCommandHelper().newHopBuilder().hopWithKeyFile(serviceVM, pathToKeyFile).build();
            fail(String.format("hopToVmHostWithNoKeyFile test step failure, " +
                            "user should not have been able to hop to %s from %s", serviceVM.getHostname(),
                    initialHost.getHostname()));
        } catch (RuntimeException e){
            assertThat(e.getMessage().contains((String.format(CLICommandHelperConstants.FILE_NOT_FOUND_MSG, pathToKeyFile))));
        }
    }

    @TestStep(id = HOP_TO_SVC_FROM_MS_AS_CLOUD_USER)
    public void hopToSvcFromMsAsCloudUser(@Input("hostname") String hostname, @Input("pathToKeyFile") String pathToKeyFile,
                                          @Input("svcHost") String svcHost, @Input("cloudUser") String cloudUser,
                                          @Input("cloudUserPassword") String cloudUserPassword, @Input("userType") UserType userType){
        final Host initialHost = DataHandler.getHostByName(hostname);
        final Host svcHostname = DataHandler.getHostByName(svcHost);
        final User userCredentials = new User(cloudUser, cloudUserPassword, userType);
        try {
            getCliCommandHelper().newHopBuilder().hopWithKeyFile(svcHostname, userCredentials, pathToKeyFile).build();
            fail(String.format("hopToSvcFromMsAsCloudUser test step failure, " +
                            "user should not have been able to hop to %s from %s", svcHostname.getHostname(),
                    initialHost.getHostname()));
        } catch (RuntimeException e){
            assertThat(e.getMessage().contains(CLICommandHelperConstants.PASSWORD_NOT_ACCEPTED_ERR_MSG));
        }
    }

    @TestStep(id=REMOVE_PMSERV_HOSTKEY)
    public void resetPmHostKey(@Input("vmHostname") String vmHostname) {
        String pmserveIp = DataHandler.getHostByName(vmHostname).getIp();
        final Host host = DataHandler.getHostByName("ms1");
        CLICommandHelper cliCommandHelper = new CLICommandHelper(host);
        cliCommandHelper.execute("ssh-keygen -R " + pmserveIp);
    }

    private void verifyHopWithKeyFile(String expectedUser, Host host) {
        String stdOut = getCliCommandHelper().execute("whoami; hostname -i");
        assertThat(stdOut).contains(expectedUser + "@");
        assertThat(stdOut).contains(host.getIp());
    }
}
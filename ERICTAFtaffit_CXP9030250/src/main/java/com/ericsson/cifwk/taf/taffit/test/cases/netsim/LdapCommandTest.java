package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

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
import com.ericsson.cifwk.taf.handlers.netsim.commands.LdapCommand;
import com.ericsson.cifwk.taf.handlers.netsim.commands.LdapCommand.Tool;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;

@Listeners(Listener.class)
public class LdapCommandTest extends TafTestBase {

    //Dummy host details for purpose of test
    private String host = "localHost";
    private String dn = "cn=Manager,dc=epol,dc=com";
    private String password = "password";

    @Inject
    private Provider<NetsimOperatorImpl> provider;
    private NetsimOperatorImpl netsimOperator;

    @BeforeMethod
    public void setUp() throws Exception {
        netsimOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_013")
    @DataDriven(name = "NodeData")
    public void testLdapBindCommand(@Input("Simulation") String simulation,
                                    @Input("NE") String nodeName) {
        if (!netsimOperator.startNe(simulation, nodeName)) {
            fail("NE " + nodeName + " was not started, check Netsim is alive and licence is valid");
        }

        LdapCommand ldapBind = NetSimCommands.ldap();
        ldapBind.setTool(Tool.BIND);
        ldapBind.setHost(host);
        ldapBind.setDn(dn);
        ldapBind.setPassword(password);
        NetSimResult results = netsimOperator.executeCommandsOnNE(simulation, nodeName, ldapBind);
        assertThat(results.getRawOutput()).contains("ldap:bind,");
    }

    @Test
    @TestId(id = "TAF_NETSIM_Func_014")
    @DataDriven(name = "NodeData")
    public void testLdapSearchCommand(@Input("Simulation") String simulation,
                                      @Input("NE") String nodeName) {
        if (!netsimOperator.startNe(simulation, nodeName)) {
            fail("NE " + nodeName + " was not started, check Netsim is alive and licence is valid");
        }
        ;
        LdapCommand ldapSearch = NetSimCommands.ldap();
        ldapSearch.setTool(Tool.SEARCH);
        ldapSearch.setHost(host);
        ldapSearch.setDn(dn);
        ldapSearch.setPassword(password);
        NetSimResult resultSearch = netsimOperator.executeCommandsOnNE(simulation, nodeName, ldapSearch);
        assertThat(resultSearch.getRawOutput()).contains("ldap:search,");
    }

}

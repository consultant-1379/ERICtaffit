/*
 * COPYRIGHT Ericsson (c) 2014.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.cifwk.taf.taffit.test.cases.scenario;

import com.beust.jcommander.internal.Lists;
import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DataSourcesTest {

    private Map<Integer, List<Integer>> vUsers = Collections.synchronizedMap(new ConcurrentHashMap());

    @BeforeMethod
    public void setUp() {
        getVUsers().clear();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0031")
    public void copiedDataSource() {
        TestDataSource<DataRecord> dataSource = fromClass(JavaDataSourceOfSize3.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("copiedDataSource")
                .withDefaultVusers(3)
                .addFlow(flow("copiedDataSource flow")
                        .addTestStep(annotatedMethod(this, "registerArgument"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);

        assertEquals(getVUsers().size(), 3);
        List<Integer> vUserIds = Lists.newArrayList(getVUsers().keySet());
        Collections.sort(vUserIds);
        Iterator<Integer> iterator = vUserIds.iterator();
        assertVUsersMap(iterator, 1, 1, 2, 3);
        assertVUsersMap(iterator, 2, 1, 2, 3);
        assertVUsersMap(iterator, 3, 1, 2, 3);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0032")
    public void contextualDataSource() {
        TestDataSource<DataRecord> csvDataSource = fromClass(JavaDataSourceOfSize3.class);
        TafTestContext.getContext().addDataSource("contextual", csvDataSource);
        TestScenario scenario = scenario("contextualDataSource")
                .withDefaultVusers(20)
                .addFlow(flow("contextualDataSource flow")
                        .addTestStep(annotatedMethod(this, "getContextualValue"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0033")
    public void globalDataSource() {
        TestContext context = TafTestContext.getContext();
        context.dataSource("global");
        TestScenario scenario = scenario("globalDataSource")
                .withDefaultVusers(20)
                .addFlow(flow("global setter flow")
                        .addTestStep(annotatedMethod(this, "setGlobalValue"))
                        .build())
                .addFlow(flow("global getter flow")
                        // asserting that value is shared within flows
                        .addTestStep(annotatedMethod(this, "getGlobalValue"))
                        .build())
                .build();
        runner().build().start(scenario);

        scenario = scenario("globalDataSource")
                .withDefaultVusers(20)
                .addFlow(flow("global getter flow")
                        // asserting that value is shared within scenarios
                        .addTestStep(annotatedMethod(this, "getGlobalValue"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0034")
    public void attribute() {
        TafTestContext.getContext().setAttribute("key", "testLevelValue");
        TestScenario scenario = scenario()
                .withDefaultVusers(20)
                .addFlow(flow("context attributes")
                        .addTestStep(annotatedMethod(this, "setAttribute"))
                        .addTestStep(annotatedMethod(this, "getAttribute"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0035", title = "vUser level attributes are not shared between scenarios")
    public void attributeSetByDifferentScenario() {
        TestScenario scenario = scenario()
                .withDefaultVusers(20)
                .addFlow(flow("context attributes flow")
                        .addTestStep(annotatedMethod(this, "setVUserAttribute"))
                        .build())
                .build();
        runner().build().start(scenario);
        runner().build().start(scenario);
    }

    @Test(enabled = false)
    @TestId(id = "TAF_Scenarios_0036", title = "context attribute set in sub-flow is not visible outside sub-flow")
    public void attributeSetInSubflow() {
        TestStepFlow flow = flow("context attributes flow")
                .addSubFlow(flow("sub-flow")
                        .addTestStep(annotatedMethod(this, "setAttribute"))
                        .build())
                .addTestStep(annotatedMethod(this, "assertNoAttribute"))
                .build();
        TestScenario scenario = scenario()
                .addFlow(flow)
                .build();
        runner().build().start(scenario);
    }

    private void assertVUsersMap(Iterator<Integer> iterator, Integer expectedVUser, Integer... expectedRecordIds) {
        Integer vUser = iterator.next();
        assertEquals(vUser, expectedVUser);
        List<Integer> recordsIds = getVUsers().get(vUser);
        assertEquals(recordsIds.size(), expectedRecordIds.length);
        for (int i = 0; i < recordsIds.size(); i++) {
            if (expectedRecordIds[i] != null) {
                assertEquals(recordsIds.get(i), expectedRecordIds[i]);
            }
        }
    }

    private synchronized Map<Integer, List<Integer>> getVUsers() {
        return vUsers;
    }

    @TestStep(id = "registerArgument")
    public synchronized void registerArgument(@Input("recordId") int recordId) {
        int vUser = TafTestContext.getContext().getVUser();
        List<Integer> arguments = getVUsers().get(vUser);
        if (arguments == null) {
            arguments = new CopyOnWriteArrayList();
            getVUsers().put(vUser, arguments);
        }
        arguments.add(recordId);
    }

    @TestStep(id = "setAttribute")
    public void setAttribute() {
        int vUser = TafTestContext.getContext().getVUser();
        if (vUser != 2) {
            TafTestContext.getContext().setAttribute("key", "value" + vUser);
        }
    }

    @TestStep(id = "getAttribute")
    public void getAttribute() {
        int vUser = TafTestContext.getContext().getVUser();
        Object actual = TafTestContext.getContext().getAttribute("key");
        if (vUser != 2) {
            assertEquals(actual, "value" + vUser);
        } else {
            assertEquals(actual, "testLevelValue");
        }
    }

    @TestStep(id = "assertNoAttribute")
    public void assertNoVUserAttribute() {
        assertNull(TafTestContext.getContext().getAttribute("key"));
    }

    @TestStep(id = "setVUserAttribute")
    public void setVUserAttribute() {
        int vUser = TafTestContext.getContext().getVUser();
        TestContext testContext = TafTestContext.getContext();
        assertNull(testContext.getAttribute("vUserAttribute"));
        TafTestContext.getContext().setAttribute("vUserAttribute", "value" + vUser);
    }

    @TestStep(id = "getContextualValue")
    public void getContextualValue() {
        TestDataSource<DataRecord> contextualDataSource = TafTestContext.getContext().getAllDataSources().get("contextual");
        Object fieldValue = contextualDataSource.iterator().next().getFieldValue("recordId");
        assertEquals("1", fieldValue);
    }

    @TestStep(id = "setGlobalValue")
    public void setGlobalValue() {
        TestDataSource<DataRecord> globalDataSource = TafTestContext.getContext().getAllDataSources().get("global");
        globalDataSource.addRecord().setField("globalKey", "globalValue");
    }

    @TestStep(id = "getGlobalValue")
    public void getGlobalValue() {
        TestDataSource<DataRecord> globalDataSource = TafTestContext.getContext().getAllDataSources().get("global");
        Object fieldValue = globalDataSource.iterator().next().getFieldValue("globalKey");
        assertEquals("globalValue", fieldValue);
    }

    public static class JavaDataSourceOfSize3 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            Map<String, Object> record = new HashMap<String, Object>();
            record.put("recordId", "1");
            records.add(record);
            record = new HashMap<String, Object>();
            record.put("recordId", "2");
            records.add(record);
            record = new HashMap<String, Object>();
            record.put("recordId", "3");
            records.add(record);
            return records;
        }
    }

}

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

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
import static org.testng.Assert.assertEquals;

public class ContextConcurrencyTest {

    private Map<Integer, AtomicInteger> vUsers = Collections.synchronizedMap(new ConcurrentHashMap());

    @BeforeMethod
    public void setUp() {
        getVUsers().clear();
        getVUsers().put(1, new AtomicInteger());
        getVUsers().put(2, new AtomicInteger());
        getVUsers().put(3, new AtomicInteger());
    }

    @Test
    @TestId(id = "TAF_Scenarios_ContextConcurrency")
    public void contextConcurrency() {
        final int UPPER_LIMIT = 100;
        for (int i = 0; i < UPPER_LIMIT; i++) {
            executeScenario();
        }

        assertEquals(getVUsers().size(), 3);
        Iterator<Integer> iterator = getVUsers().keySet().iterator();
        int vuser1 = getVUsers().get(iterator.next()).get();
        int vuser2 = getVUsers().get(iterator.next()).get();
        int vuser3 = getVUsers().get(iterator.next()).get();

        assertEquals(vuser1 + vuser2 + vuser3, UPPER_LIMIT * 3);
    }

    private void executeScenario() {
        TestDataSource<DataRecord> dataSource = fromClass(JavaDataSourceOfSize3.class);
        dataSource = TafDataSources.shared(dataSource);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("sharedDataSource")
                .withDefaultVusers(3)
                .addFlow(flow("sharedDataSource flow")
                        .addTestStep(annotatedMethod(this, "registerArgument"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @TestStep(id = "registerArgument")
    public synchronized void registerArgument(@Input("recordId") int recordId) {
        int vUser = TafTestContext.getContext().getVUser();
        getVUsers().get(vUser).incrementAndGet();
    }

    private synchronized Map<Integer, AtomicInteger> getVUsers() {
        return vUsers;
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

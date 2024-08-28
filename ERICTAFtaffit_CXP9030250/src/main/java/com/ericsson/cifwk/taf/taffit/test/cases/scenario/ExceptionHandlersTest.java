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
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.fail;

public class ExceptionHandlersTest {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private AtomicInteger exceptionsCount = new AtomicInteger();

    @BeforeMethod
    public void setUp() {
        exceptionsCount.set(0);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0050")
    public void exceptionHandlerPropogate() {

        // scenario
        TestScenario scenario = scenario()
                .withDefaultVusers(20)
                .addFlow(getExceptionHandlerPropogateFlow())
                .addFlow(getExceptionHandlerPropogateFlow())
                .addFlow(getExceptionHandlerPropogateFlow())
                .build();

        // run
        TestScenarioRunner runner = runner().withDefaultExceptionHandler(ScenarioExceptionHandler.PROPAGATE).build();
        try {
            runner.start(scenario);
            fail();
        } catch (Exception e) {
            // ignored
        }
        assertEquals(exceptionsCount.get(), 2);
    }

    private TestStepFlow getExceptionHandlerPropogateFlow() {
        return flow("exceptionHandlerPropogate")
                .addTestStep(annotatedMethod(this, "throwException"))
                .build();
    }

    @Test(enabled = false)
    @TestId(id = "TAF_Scenarios_0051")
    public void exceptionHandlerIgnore() {
        TafTestContext.getContext().addDataSource("records", TafDataSources.fromClass(RangeOf5.class));

        // scenario
        TestScenario scenario = scenario()
                .withDefaultVusers(20)
                .addFlow(flow("flow").split(
                        getExceptionHandlerIgnoreFlow(),
                        getExceptionHandlerIgnoreFlow(),
                        getExceptionHandlerIgnoreFlow()
                ))
                .build();

        // run
        TestScenarioRunner runner = runner().withDefaultExceptionHandler(ScenarioExceptionHandler.LOGONLY).build();
        runner.start(scenario);
        assertEquals(exceptionsCount.get(), 2 * 3 * 5);
    }

    private TestStepFlow getExceptionHandlerIgnoreFlow() {
        return flow("exceptionHandlerIgnore")
                .addTestStep(annotatedMethod(this, "throwException"))
                .withDataSources(dataSource("records"))
                .build();
    }

    @TestStep(id = "throwException")
    public void throwExceptionOnDemand() throws IOException {
        int vUser = TafTestContext.getContext().getVUser();
        if (vUser == 20 || vUser == 19) {
            exceptionsCount.incrementAndGet();
            throw new IOException(String.format("Checked exception was thrown by test design (vUser=%s)", vUser));
        }
    }

    public static class RangeOf5 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            for (int i = 1; i <= 5; i++) {
                Map<String, Object> record = new HashMap<String, Object>();
                record.put("recordId", i);
                records.add(record);
            }
            return records;
        }
    }

}

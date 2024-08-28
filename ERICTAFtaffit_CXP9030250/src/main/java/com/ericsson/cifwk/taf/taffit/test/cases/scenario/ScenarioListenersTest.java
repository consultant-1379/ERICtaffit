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
import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.*;
import com.ericsson.cifwk.taf.scenario.api.AbstractScenarioListener;
import com.ericsson.cifwk.taf.scenario.api.ExceptionHandler;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ScenarioListenersTest {

    private AtomicInteger scenarioStarted = new AtomicInteger();
    private AtomicInteger scenarioFinished = new AtomicInteger();
    private AtomicInteger flowStarted = new AtomicInteger();
    private AtomicInteger flowFinished = new AtomicInteger();
    private AtomicInteger testStepStarted = new AtomicInteger();
    private AtomicInteger testStepFinished = new AtomicInteger();

    @Test
    @TestId(id = "TAF_Scenarios_0070")
    public void scenarioListener() {
        TestDataSource<DataRecord> dataSource = fromClass(JavaDataSourceOfSize3.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        // 1 scenario
        TestScenario scenario = scenario()
                .withDefaultVusers(7)
                .addFlow(getExceptionHandlerPropogateFlow())
                .addFlow(getExceptionHandlerPropogateFlow())
                .build();

        // run
        TestScenarioRunner runner = runner()
                .withListener(new MyScenarioListener(), 10)
                .build();
        runner.start(scenario);

        // expectations
        final int flows = 2;
        final int records = 3;
        final int testSteps = 5;
        final int vUsers = 7;
        assertEquals(scenarioStarted.get(), 1);
        assertEquals(scenarioFinished.get(), 1);
        assertEquals(flowStarted.get(), flows * vUsers * records);  // should event really be triggered on every record?
        assertEquals(flowFinished.get(), flows * vUsers * records); // should event really be triggered on every record?
        assertEquals(testStepStarted.get(), flows * records * testSteps * vUsers);
        assertEquals(testStepFinished.get(), flows * records * testSteps * vUsers);
    }

    private TestStepFlow getExceptionHandlerPropogateFlow() {
        return flow("exceptionHandlerPropogate")
                .addTestStep(annotatedMethod(this, "doSomething"))
                .addTestStep(annotatedMethod(this, "doSomething"))
                .addTestStep(annotatedMethod(this, "doSomething"))
                .addTestStep(annotatedMethod(this, "doSomething"))
                .addTestStep(annotatedMethod(this, "doSomething"))
                .withDataSources(dataSource("records"))
                .build();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0071")
    public void scenarioListenerPriority() {

        // 2 flows
        TestStepFlow flow = flow("exceptionHandlerPropogate")
                .addTestStep(annotatedMethod(this, "emptyTestStep"))
                .build();

        // 1 scenario
        TestScenario scenario = scenario()
                .addFlow(flow)
                .build();

        // run
        ScenarioListener myfirstListener = new PriorityCheckingScenarioListener(1);
        ScenarioListener mySecondListener = new PriorityCheckingScenarioListener(2);
        TestScenarioRunner runner = runner()
                .withListener(mySecondListener, 10)
                .withListener(myfirstListener, 20)
                .build();
        runner.start(scenario);
    }

    @Test(enabled = false, expectedExceptions = RuntimeException.class)
    @TestId(id = "TAF_Scenarios_0072")
    @DataDriven(name = "range-of-6")
    public void incorrectListener(@Input("methodToThrowException") Integer methodToThrowException) {
        assertNotNull(methodToThrowException);
        TestStepFlow flow = flow("exceptionHandlerPropogate")
                .addTestStep(annotatedMethod(this, "emptyTestStep"))
                .build();
        TestScenarioRunner runner = runner()
                .withListener(new ThrowingScenarioListener(methodToThrowException))
                .withDefaultExceptionHandler(ScenarioExceptionHandler.IGNORE)
                .build();
        runner.start(scenario().addFlow(flow).build());
    }

    @TestStep(id = "doSomething")
    public void doNothing(@Input("recordId") int recordId) {
        // do nothing
    }

    @TestStep(id = "emptyTestStep")
    public void doNothing() {
        // do nothing
    }

    public static class JavaDataSourceOfSize3 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            Map<String, Object> record = new HashMap<String, Object>();
            record.put("recordId", "11");
            records.add(record);
            record = new HashMap<String, Object>();
            record.put("recordId", "11");
            records.add(record);
            record = new HashMap<String, Object>();
            record.put("recordId", "11");
            records.add(record);
            return records;
        }
    }

    public static class RangeOf6 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            for (int methodToThrowException = 1; methodToThrowException <= 6; methodToThrowException++) {
                Map<String, Object> record = new HashMap<String, Object>();
                record.put("methodToThrowException", methodToThrowException);
                records.add(record);
            }
            return records;
        }
    }

    private class MyScenarioListener implements ScenarioListener {

        @Override
        public void onScenarioStarted(TestScenario testScenario) {
            scenarioStarted.incrementAndGet();
        }

        @Override
        public void onScenarioFinished(TestScenario testScenario) {
            scenarioFinished.incrementAndGet();
        }

        @Override
        public void onFlowStarted(TestStepFlow testStepFlow) {
            flowStarted.incrementAndGet();
        }

        @Override
        public void onFlowFinished(TestStepFlow testStepFlow) {
            flowFinished.incrementAndGet();
        }

        @Override
        public void onTestStepStarted(TestStepInvocation testStepInvocation, Object[] objects) {
            testStepStarted.incrementAndGet();
            assertEquals(objects[0], 11);
        }

        @Override
        public void onTestStepFinished(TestStepInvocation testStepInvocation) {
            testStepFinished.incrementAndGet();
        }
    }

    private static class PriorityCheckingScenarioListener extends AbstractScenarioListener {

        private int id;

        private static int previousId;

        public PriorityCheckingScenarioListener(int id) {
            this.id = id;
        }

        @Override
        public void onScenarioStarted(TestScenario scenario) {
            if (id == 1) {
                assertEquals(previousId, 0);
                previousId++;
            } else {
                assertEquals(previousId, 1);
                previousId++;
            }
        }

    }

    private static class ThrowingScenarioListener extends AbstractScenarioListener {

        private int methodToThrowException;

        public ThrowingScenarioListener(int methodToThrowException) {
            this.methodToThrowException = methodToThrowException;
        }

        @Override
        public void onScenarioStarted(TestScenario testScenario) {
            if (methodToThrowException == 1) {
                throw new RuntimeException("Oops");
            }
        }

        @Override
        public void onScenarioFinished(TestScenario testScenario) {
            if (methodToThrowException == 2) {
                throw new RuntimeException("Oops");
            }
        }

        @Override
        public void onFlowStarted(TestStepFlow testStepFlow) {
            if (methodToThrowException == 3) {
                throw new RuntimeException("Oops");
            }
        }

        @Override
        public void onFlowFinished(TestStepFlow testStepFlow) {
            if (methodToThrowException == 4) {
                throw new RuntimeException("Oops");
            }
        }

        @Override
        public void onTestStepStarted(TestStepInvocation testStepInvocation, Object[] objects) {
            if (methodToThrowException == 5) {
                throw new RuntimeException("Oops");
            }
        }

        @Override
        public void onTestStepFinished(TestStepInvocation testStepInvocation) {
            if (methodToThrowException == 6) {
                throw new RuntimeException("Oops");
            }
        }

    }

}


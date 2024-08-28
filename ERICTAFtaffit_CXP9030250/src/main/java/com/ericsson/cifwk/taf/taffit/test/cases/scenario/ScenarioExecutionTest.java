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
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static org.testng.Assert.assertEquals;

public class ScenarioExecutionTest {

    public static final int WAIT_MAX_MILLIS = 100;

    private AtomicInteger counter = new AtomicInteger();

    @BeforeMethod
    public void setUp() {
        counter.set(0);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0080")
    public void parallelExecution() {
        TestScenario scenario = scenario()
                .addFlow(flow("flow").split(
                        getParallelExecutionFlow(),
                        getParallelExecutionFlow(),
                        getParallelExecutionFlow()
                ))
                .build();
        runner().build().start(scenario);
    }

    private TestStepFlow getParallelExecutionFlow() {
        return flow("parallelExecution")
                .addTestStep(annotatedMethod(this, "setCounterAndWait"))
                .addTestStep(annotatedMethod(this, "assertCounter"))
                .build();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0081")
    public void sequentialExecution() {
        TestStepFlow assertFlow = flow("asserting flow").addTestStep(annotatedMethod(this, "assertCounter")).build();
        TestScenario scenario = scenario()
                .addFlow(getSetingFlow())
                .addFlow(getSetingFlow())
                .addFlow(getSetingFlow())
                .addFlow(assertFlow)
                .build();
        runner().build().start(scenario);
    }

    private TestStepFlow getSetingFlow() {
        return flow("setting flow").addTestStep(annotatedMethod(this, "setCounterAndWait")).build();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0082")
    public void splitJoin() {
        TestStepFlow assertingFlow = flow("asserting flow").addTestStep(annotatedMethod(this, "assertCounter")).build();
        TestScenario scenario = scenario()
                .split(getSplitJoinFlow(), getSplitJoinFlow(), getSplitJoinFlow())
                .addFlow(assertingFlow)
                .build();
        runner().build().start(scenario);
    }

    private TestStepFlow getSplitJoinFlow() {
        return flow("setting flow").addTestStep(annotatedMethod(this, "setCounter")).build();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0083")
    public void overwritingVUsers() {
        TestScenario scenario = scenario()
                .withDefaultVusers(3)          // default vUsers; overwrites default one in regularFlow
                .addFlow(getRegularFlow())   // 3
                .addFlow(getFlow())          // 7 - explicit value in flow has higher priority
                .split(getFlow(), getFlow(), getFlow(), getFlow(), getFlow())                                    // 5 flows X 7 vUsers
                .split(getRegularFlow(), getRegularFlow(), getRegularFlow(), getRegularFlow(), getRegularFlow()) // 5 flows X 1 vUsers because sub flows do not inherit vUsers from parent scenario
                .addFlow(getRegularFlow())   // 3 - default value remains for regular flow
                .build();
        runner().build().start(scenario);
        assertEquals(counter.get(), 3 + 7 + (5 * 7) + (5 * 1) + 3);
    }

    private TestStepFlow getFlow() {
        return flow("flow").addTestStep(annotatedMethod(this, "setCounter")).withVusers(7).build();
    }

    private TestStepFlow getRegularFlow() {
        return flow("regular flow").addTestStep(annotatedMethod(this, "setCounter")).build();
    }

    @Test(enabled = false)
    @TestId(id = "TAF_Scenarios_0084")
    public void subflowOverwritingVUsers() {
        TestStepFlow flow = flow("complex flow")
                .addSubFlow(flow("sub-flow")
                        .withVusers(3)
                        .addTestStep(annotatedMethod(this, "setCounter"))
                        .build())
                .addTestStep(annotatedMethod(this, "assertCounter"))
                .build();
        runner().build().start(scenario().addFlow(flow).build());
    }

    @Test
    @TestId(id = "TAF_Scenarios_0085")
    public void subflowOverwritingDataSource() {
        TafTestContext.getContext().addDataSource("records", fromClass(RangeOf3.class));
        TestStepFlow flow = flow("complex flow")
                .addSubFlow(flow("sub-flow")
                        .withDataSources(dataSource("records"))
                        .addTestStep(annotatedMethod(this, "setCounter"))
                        .build())
                .addTestStep(annotatedMethod(this, "assertCounter"))
                .build();
        runner().build().start(scenario().addFlow(flow).build());
    }

    @Test
    @TestId(id = "TAF_Scenarios_0086")
    public void concurrentScenarios() throws InterruptedException {
        final int executionCount = 7 * 3 * 2;
        TafTestContext.getContext().addDataSource("records", fromClass(RangeOf3.class));
        final TestStepFlow flow1 = flow("complex flow")
                .withVusers(7)
                .withDataSources(dataSource("records"))
                .addTestStep(annotatedMethod(this, "setCounter"))
                .addTestStep(annotatedMethod(this, "waitRandom"))
                .addTestStep(annotatedMethod(this, "setAttribute"))
                .build();
        final TestStepFlow assertingFlow = flow("assert").addTestStep(annotatedMethod(this, "assertAttribute")).build();
        Thread thread1 = new ScenarioRunner(scenario().addFlow(flow1).addFlow(assertingFlow).build());
        Thread thread2 = new ScenarioRunner(scenario().addFlow(flow1).addFlow(assertingFlow).build());
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertEquals(counter.get(), executionCount);
    }

    @TestStep(id = "setCounterAndWait")
    public void setCounterAndWait() throws InterruptedException {
        // adding randomness so parallel execution test fails without test step synchronization (second sleep())
        Thread.currentThread().sleep(new Random().nextInt(100));
        setCounter();
        Thread.currentThread().sleep(300);
    }

    @TestStep(id = "setCounter")
    public void setCounter() throws InterruptedException {
        counter.incrementAndGet();
    }

    @TestStep(id = "assertCounter")
    public void assertCounterForParallel() {
        assertEquals(counter.get(), 3);
    }

    @TestStep(id = "setAttribute")
    public void setAttribute() {
        TestContext context = TafTestContext.getContext();
        int vUser = context.getVUser();
        Integer attribute = context.getAttribute("key" + vUser);
        if (attribute == null) {
            attribute = 0;
        }
        attribute++;
        context.setAttribute("key" + vUser, attribute);
    }

    @TestStep(id = "assertAttribute")
    public void assertAttribute() {
        TestContext context = TafTestContext.getContext();
        int vUser = context.getVUser();
        assertEquals(context.getAttribute("key" + vUser), 3);
    }

    @TestStep(id = "waitRandom")
    public void waitRandom() throws InterruptedException {
        Thread.sleep(new Random().nextInt(WAIT_MAX_MILLIS));
    }

    public static class RangeOf3 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            for (int recordId = 1; recordId <= 3; recordId++) {
                Map<String, Object> record = new HashMap<String, Object>();
                record.put("recordId", recordId);
                records.add(record);
            }
            return records;
        }
    }

    private static class ScenarioRunner extends Thread {

        private TestScenario testScenario;

        public ScenarioRunner(TestScenario testScenario) {
            this.testScenario = testScenario;
        }

        @Override
        public void run() {
            runner().build().start(testScenario);
        }

    }

}

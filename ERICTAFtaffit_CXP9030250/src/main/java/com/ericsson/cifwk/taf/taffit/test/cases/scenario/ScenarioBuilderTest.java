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
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class ScenarioBuilderTest {

    @Test
    @TestId(id = "TAF_Scenarios_0090")
    public void testStepParameter() {
        TestScenario scenario = scenario()
                .addFlow(getParallelExecutionFlow())
                .addFlow(getParallelExecutionFlow())
                .addFlow(getParallelExecutionFlow())
                .build();
        runner().build().start(scenario);
    }

    private TestStepFlow getParallelExecutionFlow() {
        return flow("parallelExecution")
                .addTestStep(annotatedMethod(this, "testStepWithParameters").withParameter("param1", "value1").withParameter("param2", "value2"))
                .addTestStep(annotatedMethod(this, "testStepWithParameter").withParameter("param1", "value1").withParameter("param2", "value2"))
                .build();
    }

    @Test
    @TestId(id = "TAF_Scenarios_0091")
    public void testStepParametersMixedWithDataSource() {
        TafTestContext.getContext().addDataSource("dsOfSize3", TafDataSources.fromClass(JavaDataSourceOfSize3.class));
        TestScenario scenario = scenario()
                .addFlow(getParallelExecutionFlowWithParams())
                .addFlow(getParallelExecutionFlowWithParams())
                .addFlow(getParallelExecutionFlowWithParams())
                .build();
        runner().build().start(scenario);
    }

    private TestStepFlow getParallelExecutionFlowWithParams() {
        return flow("parallelExecution")
                .addTestStep(annotatedMethod(this, "testStepWithMixedParameters")
                        .withParameter("param", "scenarioParamValue")
                        .withParameter("mixedParam", "scenarioValue"))
                .withDataSources(dataSource("dsOfSize3"))
                .build();
    }

    @TestStep(id = "testStepWithParameter")
    public void testStepWithParameter(@Input("param2") String param) {
        assertEquals(param, "value2");
    }

    @TestStep(id = "testStepWithParameters")
    public void testStepWithParameters(@Input("param1") String param1, @Input("param2") String param2) {
        assertEquals(param1, "value1");
        assertEquals(param2, "value2");
    }

    @TestStep(id = "testStepWithMixedParameters")
    public void testStepWithMixedParameters(@Input("mixedParam") String mixedParam,
                                            @Input("param") String param,
                                            @Input("recordId") int recordId) {
        assertNotEquals(mixedParam, "dataSourceValue" + recordId);
        assertNotEquals(param, "dataSourceValue");
    }

    public static class JavaDataSourceOfSize3 {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> record = new HashMap<String, Object>();
                record.put("recordId", "" + i);
                record.put("mixedParam", "dataSourceValue" + i);
                if (i == 1) {
                    record.put("param", "dataSourceValue");
                }
                records.add(record);
            }
            return records;
        }
    }

}

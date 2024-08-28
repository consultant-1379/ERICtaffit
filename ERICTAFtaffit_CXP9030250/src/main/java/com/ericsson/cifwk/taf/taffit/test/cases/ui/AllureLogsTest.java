package com.ericsson.cifwk.taf.taffit.test.cases.ui;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps;
import org.testng.annotations.Test;
import javax.inject.Inject;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps.OPEN_ALLURE;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps.SELECT_TESTCASE;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps.SELECT_TESTSUITE;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps.SELECT_XUNIT;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.AllureLogs_TestSteps.VERIFY_LOG_EXISTS;

public class AllureLogsTest extends TafTestBase {

    @Inject
    AllureLogs_TestSteps testSteps;

    /*
    * disabled as this will never pass as it is
    * the url links to the most recent allure report which returns a 404 during an execution
     */
    @Test(enabled = false)
    @TestId(id = "TAF_Func_001")
    public void testAllureLogs() {
        TestScenario scenario = scenario("Verify logs.txt is included in Allure Report").addFlow(allureLogsFlow()).build();
        TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }

    public TestStepFlow allureLogsFlow(){
        return flow("AllureLogs")
                .addTestStep(annotatedMethod(testSteps, OPEN_ALLURE))
                .addTestStep(annotatedMethod(testSteps, SELECT_XUNIT))
                .addTestStep(annotatedMethod(testSteps, SELECT_TESTSUITE))
                .addTestStep(annotatedMethod(testSteps, SELECT_TESTCASE))
                .addTestStep(annotatedMethod(testSteps, VERIFY_LOG_EXISTS))
                .build();
    }
}

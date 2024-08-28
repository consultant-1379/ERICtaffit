package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.enm.Tool;
import com.ericsson.enm.UserSession;
import com.google.inject.Provider;
import javax.inject.Inject;

import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertEquals;
import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertTrue;

public class AllureLogs_TestSteps {

    @Inject
    private Provider<AllureLogsOperatorImpl> webOperatorProvider;

    @Inject
    public TestContext context;

    private AllureLogsOperatorImpl allureLogsOperator;

    public static final String OPEN_ALLURE = "open-allure";
    public static final String SELECT_XUNIT = "select-xUnit";
    public static final String SELECT_TESTSUITE = "select-TestSuite";
    public static final String SELECT_TESTCASE = "select-TestCase";
    public static final String VERIFY_LOG_EXISTS = "verifyLogExists";

    @TestStep(id = "open-allure")
    public void openMainAllurePage() {
        Tool tool = webOperatorProvider.get().openLoginPage();
        UserSession userSession = new UserSession();
        userSession.setTool(tool);
        context.setAttribute(UserSession.SESSION, userSession);
        AllureLogsOperatorImpl allure = webOperatorProvider.get();
        allure.openLoginPage();
        assertEquals(allure.getCurrentUrl(), DataHandler.getAttribute("allureMainPage"));
    }

    @TestStep(id = "select-xUnit")
    public void selectXUnit() {
        getOperator().selectXUnit();
    }

    @TestStep(id = "select-TestSuite")
    public void selectTestSuite() {
        getOperator().selectTestSuite();
    }

    @TestStep(id = "select-TestCase")
    public void selectTestCase() {
        getOperator().selectTestCase();
    }

    @TestStep(id = "verifyLogExists")
    public void verifyLogExists() {
        assertTrue(getOperator().verifyLogExists());
    }

    private AllureLogsOperatorImpl getOperator() {
        allureLogsOperator = webOperatorProvider.get();
        UserSession userSession = context.getAttribute(UserSession.SESSION);
        allureLogsOperator.setTool(userSession.getTool());
        return allureLogsOperator;
    }
}

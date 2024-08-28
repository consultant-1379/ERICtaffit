/*
 * COPYRIGHT Ericsson (c) 2015.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.cifwk.taf.taffit.test.cases.http;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.ericsson.cifwk.taf.Constants.UISDK_VAPP_HOST;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.AUTHENTICATE;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.AUTHENTICATE_WRONG_CREDENTIALS;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.INSTANTIATE_HTTP_TOOL;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.QUERY_WITH_PARAMS;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_DELETE;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_GET;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_POST;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_PUT;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_SMALL_TIMEOUT;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.REQUEST_WITH_TIMEOUT;

public class HttpToolTest extends TafTestBase {
    @Inject
    Provider<HttpToolOperator> provider;

    HttpToolOperator httpToolOperator;

    private Logger logger = LoggerFactory.getLogger(HttpToolTest.class);

    @BeforeSuite
    public void installHttpBin(){
        Host host = DataHandler.getHostByName(UISDK_VAPP_HOST);
        HttpTool tool = HttpToolBuilder.newBuilder(host.getIp()).build();
        try {
            final HttpResponse response = tool.get("/get");
            if (response.getResponseCode().equals(HttpStatus.OK)) {
                logger.info("HttpBin has already been installed and the connection is in use");
            } else {
                installHttpBin(host);
            }
        } catch (Exception e){
            installHttpBin(host);
        }
    }

    private void installHttpBin(Host host) {
        CLICommandHelper cmdHelper = new CLICommandHelper(host);
        cmdHelper.openShell();
        cmdHelper.execute("export http_proxy=http://atproxy1.athtem.eei.ericsson.se:3128");
        cmdHelper.execute("export https_proxy=http://atproxy1.athtem.eei.ericsson.se:3128");
        cmdHelper.execute("sudo rpm -ivh http://download.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm");
        cmdHelper.execute("iptables -I INPUT -p tcp --dport 5000 -j ACCEPT");
        cmdHelper.execute("yum -y install python-pip");
        cmdHelper.execute("pip install httpbin==v0.4.1 gunicorn");
        cmdHelper.interactWithShell("gunicorn httpbin:app --bind=0.0.0.0:5000&");
        cmdHelper.execute("disown");
        cmdHelper.disconnect();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        httpToolOperator = provider.get();
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_016")
    public void testAuthentication() {
        TestScenario scenario = scenario("authentication").addFlow(
                flow("authentication").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, AUTHENTICATE_WRONG_CREDENTIALS))
                        .addTestStep(annotatedMethod(httpToolOperator, AUTHENTICATE)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_007")
    public void testQueryParam() {
        TestScenario scenario = scenario("queryParam").addFlow(
                flow("queryParam").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, QUERY_WITH_PARAMS)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_017")
    public void testTimeout() {
        TestScenario scenario = scenario("timeout").addFlow(
                flow("timeout").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_SMALL_TIMEOUT))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_WITH_TIMEOUT)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_001")
    public void testGet() {
        TestScenario scenario = scenario("httpGet").addFlow(
                flow("httpGet").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_GET)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_002")
    public void testPost() {
        TestScenario scenario = scenario("httpPost").addFlow(
                flow("httpPost").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_POST)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_003")
    public void testDelete() {
        TestScenario scenario = scenario("httpDelete").addFlow(
                flow("httpDelete").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_DELETE)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_005")
    public void testPut() {
        TestScenario scenario = scenario("httpPut").addFlow(
                flow("httpPut").addTestStep(annotatedMethod(httpToolOperator, INSTANTIATE_HTTP_TOOL))
                        .addTestStep(annotatedMethod(httpToolOperator, REQUEST_PUT)).build()).build();

        createRunner().start(scenario);
    }

    @Test
    @TestId(id = "TAF_HTTP_Tool_Functional_018")
    public void testRedirect(){
        TestScenario scenario = scenario("http redirect").addFlow(flow("http redirect").addTestStep(annotatedMethod
                (httpToolOperator, INSTANTIATE_HTTP_TOOL)).addTestStep(annotatedMethod(httpToolOperator, HttpToolTestSteps.REDIRECT)))
                                                         .build();

        createRunner().start(scenario);
    }

    private TestScenarioRunner createRunner() {
        return runner().withListener(new LoggingScenarioListener()).build();
    }
}

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

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.cifwk.taf.Constants.UISDK_VAPP_HOST;
import static com.ericsson.cifwk.taf.taffit.test.cases.http.HttpToolTestSteps.*;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.testng.AssertJUnit.fail;

@Operator
public class HttpToolOperator {
    public static final String HTTP_TOOL = "httpTool";

    @Inject
    TestContext context;

    @TestStep(id = INSTANTIATE_HTTP_TOOL)
    public void instantiateHttpTool() {
        Host host = DataHandler.getHostByName(UISDK_VAPP_HOST);
        HttpTool tool = HttpToolBuilder.newBuilder(host).build();

        context.setAttribute(HTTP_TOOL, tool);
    }

    @TestStep(id = AUTHENTICATE_WRONG_CREDENTIALS)
    public void authenticateWrongCredentials() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final String USERNAME = "username";
        final String PASSWORD = "password";
        final HttpResponse response = tool.request()
                .authenticate(USERNAME, PASSWORD)
                .get(format("/basic-auth/%s/%s", USERNAME, "wrong"));

        assertThat(response.getResponseCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @TestStep(id = AUTHENTICATE)
    public void authenticate() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final String USERNAME = "username";
        final String PASSWORD = "password";
        final HttpResponse response = tool.request()
                .authenticate(USERNAME, PASSWORD)
                .get(format("/basic-auth/%s/%s", USERNAME, PASSWORD));

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
    }

    @TestStep(id = QUERY_WITH_PARAMS)
    public void queryWithParams() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final String PARAM_1 = "param1";
        final String PARAM_2 = "param2";
        final HttpResponse response = tool.request()
                .queryParam(PARAM_1, "value1")
                .queryParam(PARAM_2, "value2", "value3", "value4")
                .get("/get");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        Map<String, Object> retMap = readJson(response.getBody());

        final Map<String, Object> args = cast(retMap.get("args"));
        List<String> param2 = cast(args.get(PARAM_2));

        assertThat(args, hasEntry(PARAM_1, cast("value1")));
        assertThat(param2, contains("value2", "value3", "value4"));
    }

    @TestStep(id = REQUEST_SMALL_TIMEOUT)
    public void requestSmallTimeout() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final int TIMEOUT = 2;

        final HttpResponse response = tool.request()
                .timeout(TIMEOUT + 2)
                .get("/delay/" + (TIMEOUT));
        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
    }

    @TestStep(id = REQUEST_WITH_TIMEOUT)
    public void requestWithTimeout() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final int TIMEOUT = 2;
        try {
            tool.request()
                    .timeout(TIMEOUT)
                    .get("/delay/" + (TIMEOUT + 2));
            fail("Exception not thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), equalTo("Connection timeout"));
        }
    }

    @TestStep(id = REQUEST_GET)
    public void requestGet() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final HttpResponse response = tool.get("/get");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders(), hasEntry("Content-Type", ContentType.APPLICATION_JSON));
        assertThat(response.getBody(), containsString(DataHandler.getHostByName(UISDK_VAPP_HOST).getIp()));
    }

    @TestStep(id = REQUEST_POST)
    public void requestPost() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final HttpResponse response = tool.post("/post");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders(), hasEntry("Content-Type", ContentType.APPLICATION_JSON));
        assertThat(response.getBody(), containsString(DataHandler.getHostByName(UISDK_VAPP_HOST).getIp()));
    }

    @TestStep(id = REQUEST_DELETE)
    public void requestDelete() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final HttpResponse response = tool.delete("/delete");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders(), hasEntry("Content-Type", ContentType.APPLICATION_JSON));
        assertThat(response.getBody(), containsString(DataHandler.getHostByName(UISDK_VAPP_HOST).getIp()));
    }

    @TestStep(id = REQUEST_PUT)
    public void requestPut() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final HttpResponse response = tool.put("/put");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders(), hasEntry("Content-Type", ContentType.APPLICATION_JSON));
        assertThat(response.getBody(), containsString(DataHandler.getHostByName(UISDK_VAPP_HOST).getIp()));
    }

    @TestStep(id = REDIRECT)
    public void redirect() {
        final HttpTool tool = context.getAttribute(HTTP_TOOL);

        final HttpResponse response = tool.get("/redirect-to?url=http://www.google.ie");

        assertThat(response.getResponseCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), containsString("<title>Google</title>"));
    }

    private Map<String, Object> readJson(String json) {
        return new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    private <T> T cast(Object cast) {
        return (T) cast;
    }
}

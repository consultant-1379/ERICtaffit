package com.ericsson.cifwk.taf.taffit.test.cases.http;
/*
 * COPYRIGHT Ericsson (c) 2015.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

public interface HttpToolTestSteps {
    String INSTANTIATE_HTTP_TOOL = "instantiateHttpTool";
    String AUTHENTICATE_WRONG_CREDENTIALS = "authenticateWrongCredentials";
    String AUTHENTICATE = "authenticate";
    String QUERY_WITH_PARAMS = "queryWithParams";
    String REQUEST_SMALL_TIMEOUT = "requestSmallTimeout";
    String REQUEST_WITH_TIMEOUT = "requestWithTimeout";
    String REQUEST_GET = "requestGet";
    String REQUEST_POST = "requestPost";
    String REQUEST_DELETE = "requestDelete";
    String REQUEST_PUT = "requestPut";
    String REDIRECT = "redirect";
}

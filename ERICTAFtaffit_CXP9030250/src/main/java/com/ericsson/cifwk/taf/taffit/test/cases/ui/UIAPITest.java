package com.ericsson.cifwk.taf.taffit.test.cases.ui;


import static com.google.common.truth.Truth.assertThat;

import javax.inject.Inject;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.SampleWebAppOperator;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.SampleWebAppUiOperator;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.google.inject.Provider;

/**
 * Created by ejambuc on 18/03/2015.
 */

public class UIAPITest extends TafTestBase {

    @Inject
    Provider<SampleWebAppUiOperator> provider;

    @Inject
    private TafConfiguration configuration;

    private SampleWebAppOperator operator;

    private static final String LOGIN_URL = "loginUrl";
    private static final String MAIN_PAGE_URL = "mainPageUrl";

    @Test
    @TestId(id = "TAF_UI_Func_1")
    public void verifyGetURLFromBrowser() {
        operator = provider.get();
        operator.openLoginPage();
        assertThat(operator.getCurrentUrl()).isEqualTo(configuration.getString(LOGIN_URL));
        operator.enterLoginDetails();
        assertThat(operator.getCurrentUrl()).isEqualTo(configuration.getString(MAIN_PAGE_URL));
    }

   /* @Test
    @TestId(id = "TAF_UI_Func_2")
    public void verifiyCookiesReturnedFromPage(){
        //Does not work - no cookies returned
        operator = operatorRegistry.provide(SampleWebAppOperator.class);
        operator.login();
        Set<BrowserCookie> cookies = new HashSet<BrowserCookie>();
        assertEquals(operator.getCookies(), cookies);
    }*/

    /*@Test
    @TestId(id = "TAF_UI_Func_3")
    public void verifyUserCanInteractWithCommonControls(){
        //Does not work - drag and drop not functioning
        operator = operatorRegistry.provide(SampleWebAppOperator.class);
        operator.login();
        operator.enterMemberDetails();
        operator.saveMember();
    }*/

    @Test
    @TestId(id = "TAF_UI_Func_4")
    public void verifyUserCanInteractWithMultipleTableRows() {
        operator = provider.get();
        operator.login();
        operator.showNumberOfRecords(20);
        assertThat(operator.numberOfRecords()).isEqualTo(15);
        operator.deleteMemberRows("Ovchinnikov", "Nikolaenko");
        operator.deleteMember();
        operator.dismissMessageBox(".ebBtn.ebBtn_color_green");
        assertThat(operator.numberOfRecords()).isEqualTo(13);
    }

    @Test
    @TestId(id = "TAF_UI_Func_5")
    public void verifyUserCanNavigateWithBrowserNavigationButtons() {
        operator = provider.get();
        operator.login();
        assertThat(operator.getCurrentUrl()).isEqualTo(configuration.getString("mainPageUrl"));
        operator.navigateBack("#Container");
        assertThat(operator.getCurrentUrl()).isEqualTo(configuration.getString("loginUrl"));
        operator.navigateForward(".eaMembersApp-Links");
        assertThat(operator.getCurrentUrl()).isEqualTo(configuration.getString("mainPageUrl"));
    }

    @Test
    @TestId(id = "TAF_UI_Func_6")
    public void verifyUserCanSuccessfullyRefreshPage() {
        operator = provider.get();
        operator.login();
        operator.enterMemberDetails();
        operator.refresh(".ebInput.eaMembersApp-MemberForm-input.eaMembersApp-MemberForm-name");
        assertThat(operator.fieldsReset()).isTrue();
    }

    @Test
    @TestId(id = "TAF_UI_Func_7")
    public void verifyUserCanGetMessageBox() {
        operator = provider.get();
        operator.login();
        operator.deleteMember();
        assertThat(operator.isMessageBoxDisplayed()).isTrue();
        operator.dismissMessageBox(".ebBtn.ebBtn_color_blue");
        assertThat(operator.isMessageBoxDisplayed()).isFalse();
    }

    @Test
    @TestId(id = "TAF_UI_Func_8")
    public void verifyUserCanGetDialogBox() {
        operator = provider.get();
        operator.login();
        operator.deleteMemberRows("Ovchinnikov");
        operator.deleteMember();
        operator.dismissMessageBox(".ebBtn.ebBtn_color_green");
    }

    /*
    @Test
    @TestId(id= "TAF_UI_Func_9")
    private void verifyUserCanImportFileUsingImportControl(){
    //Doesn't work - File import cannot work as the dialog is outside the DOM and selenium cannot interact with it
        operator = operatorRegistry.provide(SampleWebAppOperator.class);
        operator.login();
        operator.importFile();
        assertTrue(operator.importSuccessful());
    }*/

    @Test
    @TestId(id = "TAF_UI_Func_10")
    public void verifyUserCanCopyAndPaste() {
        operator = provider.get();
        operator.login();
        operator.enterMemberDetails();
        operator.copyAndPaste();
        assertThat(operator.fieldsContainSameValue()).isTrue();
    }

    @Test(dataProvider = "browserType")
    @TestId(id = "TAF_UI_Func_23")
    public void verifyUserCanFocusTextField(BrowserType browserType) throws Exception {
        operator = provider.get();
        operator.login(browserType);
        operator.navigateToAddMemberScreen();
        assertThat(operator.isFirstNameFieldFocused()).isFalse();

        operator.focusFirstNameField();
        assertThat(operator.isFirstNameFieldFocused()).isTrue();

        operator.typeText("John");
        assertThat(operator.getFirstName()).contains("John");
    }

    @DataProvider(name = "browserType")
    public Object[][] getBrowserType() {
        return new Object[][] {
                { BrowserType.FIREFOX },
//                { BrowserType.CHROME }, // Disabled until new robust Chrome driver is available in the Grid. See http://jira-nam.lmera.ericsson.se/browse/CIS-10694
//                { BrowserType.PHANTOMJS } // PhantomJS is not installed in the current Grid.
        };
    }

    @Test
    @TestId(id = "TAF_UI_Func_25")
    public void testUiComponentEvaluate() {
        String simpleJavascriptCommand = "return element.innerHTML;";
        operator = provider.get();
        operator.login();
        UiComponent table = operator.getTable();
        UiComponent tableBody = operator.getTableBody();
        UiComponent tableByXpath = operator.getTableByXpath();

        String tableInnerHtml = table.evaluate(simpleJavascriptCommand).toString();
        String tableBodyInnerHtml = tableBody.evaluate(simpleJavascriptCommand).toString();
        String tableByXpathInnerHtml = tableByXpath.evaluate(simpleJavascriptCommand).toString();

        operator.clickNextPageButton();

        String tableInnerHtmlPage2 = table.evaluate(simpleJavascriptCommand).toString();

        assertThat(tableInnerHtml.trim()).startsWith("<thead class=\"elTablelib-Table-header\"><tr class=\"ebTableRow\">");
        assertThat(tableInnerHtml.trim()).endsWith("@ericsson.com</a></td><td title=\"Cinema\">Cinema</td></tr></tbody>");
        assertThat(tableBodyInnerHtml.trim()).startsWith("<tr data-id=\"1\" class=\"ebTableRow\"><td class=\"elTablelib-CheckboxCell\">");
        assertThat(tableBodyInnerHtml.trim()).endsWith("@ericsson.com</a></td><td title=\"Cinema\">Cinema</td></tr>");
        assertThat(tableByXpathInnerHtml).isEqualTo(tableInnerHtml);
        assertThat(tableInnerHtmlPage2.trim()).startsWith("<thead class=\"elTablelib-Table-header\"><tr class=\"ebTableRow\">");
        assertThat(tableInnerHtmlPage2.trim()).endsWith("@ericsson.com</a></td><td title=\"UI SDK\">UI SDK</td></tr></tbody>");
    }

}

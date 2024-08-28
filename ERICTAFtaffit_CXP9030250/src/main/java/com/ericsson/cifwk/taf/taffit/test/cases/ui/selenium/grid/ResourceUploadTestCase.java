package com.ericsson.cifwk.taf.taffit.test.cases.ui.selenium.grid;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.BrowserSetup;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.DesktopNavigator;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.sdk.ViewModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 01/10/2015
 *         <p/>
 */
public class ResourceUploadTestCase {

    private Browser browser;

    @BeforeMethod
    public void setUp() {
        BrowserSetup.Builder builder = new BrowserSetup.Builder()
                .withType(BrowserType.FIREFOX)
                .withOS(BrowserOS.ANY);

        //acquire session on selenium grid
        browser = UI.newBrowserWithImageRecognition(builder);
    }

    @Test(enabled = false)
    @TestId(id = "TAF_UI_Func_15", title = "Verify resources for sikuli image recognition are uploaded to Selenium grid node")
    public void verifyResourceUploadToSeleniumNode() {
        BrowserTab currentWindow = browser.open("http://www.ericsson.com/thecompany");

        // Sikuli test
        DesktopNavigator sikuli = UI.newDesktopNavigator(browser, "sikuli");
        ViewModel screen = sikuli.getCurrentWindow().getGenericView();

        //clicking main logo navigates to home
        UiComponent viewComponent = screen.getViewComponent("ericsson_logo.png");
        assertThat(viewComponent.exists(), is(true));
        viewComponent.click();

        String currentUrl = browser.getCurrentWindow().getCurrentUrl();
        assertThat(currentUrl, is("http://www.ericsson.com/"));
    }
}

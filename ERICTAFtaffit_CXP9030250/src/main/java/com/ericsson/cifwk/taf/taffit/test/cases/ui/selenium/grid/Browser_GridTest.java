package com.ericsson.cifwk.taf.taffit.test.cases.ui.selenium.grid;

import java.util.Arrays;
import java.util.Collection;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.SelectorType;

import static com.google.common.truth.Truth.assertThat;

public class Browser_GridTest extends TafTestBase {

    BrowserType browserType;
    Browser browser;

    public Browser_GridTest() {
        this.browserType = BrowserType.FIREFOX;
    }

    public Browser_GridTest(BrowserType browserType) {
        this.browserType = browserType;
    }

    @Parameters(value = "browser={0}")
    public static Collection<Object[]> primeNumbers() {
        return Arrays.asList(new Object[][]{
                {BrowserType.FIREFOX},
                {BrowserType.CHROME}
        });
    }

    protected String findHtmlPage(String html) {
        return "https://www.google.com/#q=" + html;   
    }

    @BeforeMethod
    public void setUp() throws Exception {
        if(browserType == null){
            browserType = BrowserType.FIREFOX;
        }
        browser = UI.newBrowser(browserType);
    }

    @Test
    @TestId(id = "TAF_UI_Func_16")
    public void takesScreenshot() throws Exception {
        String firstPage = findHtmlPage("first.htm");
        BrowserTab first = browser.open(firstPage);
        String screenshot = "shot-" + "browserType" + ".png";
        first.takeScreenshot(screenshot);
    }

/*
    @Test
    @TestId(id = "TAF_UI_Func_18")
    public void shouldOpenURLInSameTab() throws Exception {
        String firstPage = findHtmlPage("first.htm");
        BrowserTab tab = browser.open(firstPage);
        tab.waitUntilComponentIsDisplayed(SelectorType.CSS, "#logocont", 10000);
        assertThat(tab.getTitle()).contains("first");
        assertThat(browser.getOpenedWindowsAmount()).isEqualTo(1);

        String secondPage = findHtmlPage("second.htm");
        tab.open(secondPage);
        tab.waitUntilComponentIsDisplayed(SelectorType.CSS, "#logocont", 10000);
        // test is loading logocont too quickly on opening same page and title isn't updated in time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(tab.getTitle()).contains("second");
        assertThat(browser.getOpenedWindowsAmount()).isEqualTo(1);
    }*/
}
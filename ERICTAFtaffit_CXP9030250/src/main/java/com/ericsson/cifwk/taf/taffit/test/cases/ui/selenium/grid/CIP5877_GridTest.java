package com.ericsson.cifwk.taf.taffit.test.cases.ui.selenium.grid;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserSetup;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.UiComponentSize;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * CIP-5795 As a TAF UI user I would like to change browser proxy settings & browser resolution <br />
 * <p/>
 * See: <a href="http://jira-oss.lmera.ericsson.se/browse/CIP-5795">jira:CIP-5795</a>
 */
public class CIP5877_GridTest extends TafTestBase{

    BrowserType browserType;
    Browser browser;

    public CIP5877_GridTest() {
    }

    public CIP5877_GridTest(BrowserType browserType) {
        this.browserType = browserType;
    }

    @Parameters(value = "browser={0}")
    public static List<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                // No Chrome for the moment, as there are problems with the Chrome on grids
                // {BrowserType.CHROME},
                {BrowserType.FIREFOX}
        });
    }


    @BeforeMethod
    public void setUp() throws Exception {
        browser = UI.newBrowser(browserType, BrowserSetup.Resolution.RESOLUTION_800x600);
    }

    protected BrowserTab open(String url) {
        return browser.open("https://www.google.com/#q=" + url);
    }

    @Test
    @TestId(id = "TAF_UI_Func_19")
    public void shouldCreateBrowser_with_ScreenResolution() throws Exception {
        BrowserTab tab = open("first.htm");
        assertThat(tab.getTitle().toLowerCase()).contains("google");
        UiComponentSize size = tab.getSize();
        assertThat(size.getWidth()).isEqualTo(800);
        assertThat(size.getHeight()).isEqualTo(600);
    }

    @Test
    @TestId(id = "TAF_UI_Func_20")
    public void shouldChangeWindowsSize() throws Exception {
        browser.setSize(720, 480);
        BrowserTab tab = open("first.htm");
        assertThat(tab.getTitle().toLowerCase()).contains("google");
        UiComponentSize size = tab.getSize();
        assertThat(size.getWidth()).isEqualTo(720);
        assertThat(size.getHeight()).isEqualTo(480);
    }
}

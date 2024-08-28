package com.ericsson.cifwk.taf.taffit.test.cases.ui.selenium.grid;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.BrowserSetup;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;

/**
 * CIP-5878 Create support for switching proxy
 * <p/>
 * See: <a href="http://jira-oss.lmera.ericsson.se/browse/CIP-5878">jira:CIP-5878</a>
 */
public class CIP5878_GridTest extends TafTestBase {

    private final BrowserOS browserOs;
    private final BrowserType browserType;
    private final String connectionErrorMsg;

    private BrowserSetup.Builder woProxy;
    private BrowserSetup.Builder withProxy;

    public CIP5878_GridTest() {
        this.browserOs = BrowserOS.LINUX;
        this.browserType = BrowserType.FIREFOX;
        this.connectionErrorMsg = "Problem loading page";
    }

    public CIP5878_GridTest(BrowserOS browserOs, BrowserType browserType, String connectionErrorMsg) {
        this.browserOs = browserOs;
        this.browserType = browserType;
        this.connectionErrorMsg = connectionErrorMsg;
    }

    @Parameters(value = "os={0}, browser={1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {BrowserOS.LINUX, BrowserType.FIREFOX, "Problem loading page"},
                {BrowserOS.LINUX, BrowserType.CHROME, "is not available"}
        });
    }

    @BeforeMethod
    public void setUp() throws Exception {
        withProxy = BrowserSetup.build()
                .withType(browserType)
                .withOS(browserOs)
                .withCapability(CapabilityType.PROXY,
                        new Proxy().setHttpProxy("localhost:8080"));
        woProxy = BrowserSetup.build()
                .withOS(browserOs);
    }

    protected String page(String url) {
        return "http://www.google.com/#q=" + url;
    }

    @Test
    @TestId(id = "TAF_UI_Func_21")
    public void shouldAllowProxyForBrowser() throws Exception {
        Browser b1 = UI.newBrowser(withProxy.withType(browserType));
        Browser b2 = UI.newBrowser(woProxy.withType(browserType));
        BrowserTab t1 = b1.open(page("first.htm"));
        BrowserTab t2 = b2.open(page("first.htm"));
        assertThat(t1.getTitle()).contains(connectionErrorMsg);
        assertThat(t2.getTitle()).contains("first");

    }

    @Test
    @TestId(id = "TAF_UI_Func_22")
    public void shouldDisAllowProxyForIEXPLORER() {
        try {
            UI.newBrowser(withProxy.withType(BrowserType.IEXPLORER));
            Assert.fail("Taf UI SDK can't support proxy setting for Internet explorer");
        } catch (IllegalArgumentException expected) {
            assertThat(browserOs).isNotEqualTo(BrowserOS.WINDOWS);
        } catch (UnsupportedOperationException expected) {
            assertThat(browserOs).isEqualTo(BrowserOS.WINDOWS);
        }
    }
}

package com.ericsson.cifwk.taf.taffit.test.cases.ui.selenium.grid;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.BrowserSetup;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.extension.GridFileDownloadRequest;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 01/10/2015
 *         <p/>
 */
public class FileDownloadTestCase {

    private Browser browser;

    @BeforeMethod
    public void setUp() {
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("browser.download.folderList", 2);
        firefoxProfile.setPreference("browser.download.dir", "/tmp/selenium-downloads");
        firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
        firefoxProfile.setPreference("pdfjs.disabled", true);
        firefoxProfile.setPreference("plugin.scan.plid.all", false);
        firefoxProfile.setPreference("plugin.scan.Acrobat", "99.0");

        BrowserSetup.Builder builder = new BrowserSetup.Builder()
                .withType(BrowserType.FIREFOX)
                .withOS(BrowserOS.ANY)
                .withCapability(FirefoxDriver.PROFILE, firefoxProfile);

        //acquire session on selenium grid
        browser = UI.newBrowser(builder);
    }

    @Test
    @TestId(id = "TAF_UI_Func_14", title = "Verify file download from Selenium grid node")
    public void verifySavedByBrowserFileDownload() {
        //We download report from this page, with file name - pdf.pdf
        browser.open("http://www.pdf995.com/samples/pdf.pdf");

        GridFileDownloadRequest remoteFileDownloadRequest = new GridFileDownloadRequest(browser);
        File download = remoteFileDownloadRequest.download("/tmp/selenium-downloads/pdf.pdf");

        assertThat(download.exists(), is(true));
        System.out.println("Path for file" + download.getAbsolutePath());
        System.out.println("Length of downloaded pdf file is:" + download.length());

        download.deleteOnExit();
    }
}

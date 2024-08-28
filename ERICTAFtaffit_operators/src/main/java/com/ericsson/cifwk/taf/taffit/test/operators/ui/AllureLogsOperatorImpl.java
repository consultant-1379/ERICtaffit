package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.taffit.test.pages.AllureMainPageViewModel;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.sdk.ViewModel;
import com.ericsson.enm.Tool;

public class AllureLogsOperatorImpl {

    private Browser browser;
    private BrowserTab browserTab;
    private ViewModel genericViewModel;
    private AllureMainPageViewModel allureModel;

    public void setTool(Tool tool) {
        this.browser = tool.getAs(Browser.class);
        this.browserTab = browser.getCurrentWindow();
    }

    public Tool openLoginPage() {
        browser = UI.newBrowser(BrowserType.FIREFOX, BrowserOS.LINUX);
        browserTab = browser.open((String) DataHandler.getAttribute("allureMainPage"));
        genericViewModel = browserTab.getGenericView();
        return Tool.set(browser);
    }

    public String getCurrentUrl() {
        return browserTab.getCurrentUrl();
    }

    public void selectXUnit(){
        allureModel = browserTab.getView(AllureMainPageViewModel.class);
        allureModel.selectTab("xUnit");
    }

    public void selectTestSuite(){
        allureModel = browserTab.getView(AllureMainPageViewModel.class);
        allureModel.selectTestSuite("TAF_UI_SDK_Suite");
    }

    public void selectTestCase(){
        allureModel = browserTab.getView(AllureMainPageViewModel.class);
        allureModel.selectTestCase("message");
    }

    public Boolean verifyLogExists(){
        allureModel = browserTab.getView(AllureMainPageViewModel.class);
        return allureModel.verifyLogExists();
    }
}

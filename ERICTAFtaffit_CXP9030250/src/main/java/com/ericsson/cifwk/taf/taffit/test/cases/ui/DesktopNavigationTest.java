package com.ericsson.cifwk.taf.taffit.test.cases.ui;

import static org.testng.Assert.assertTrue;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.BrowserSetup;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.DesktopNavigator;
import com.ericsson.cifwk.taf.ui.DesktopWindow;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.GenericPredicate;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentNotFoundException;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.cifwk.taf.ui.sdk.ViewModel;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         05/10/2015
 */
public class DesktopNavigationTest extends TafTestBase{

    private static final String SAMPLE_IMAGES_BUNDLE = "images/sample-web-app";

    private static final String USERNAME_FIELD = "username_field.png";
    private static final String PASSWORD_FIELD = "password_field.png";
    private static final String LOGIN_BUTTON= "login_button.png";
    private static final String LOGOUT_BUTTON = "logout_button.png";
    private static final String OK_BUTTON = "ok_button.png";

    private Browser browser;

    @Inject
    private TafConfiguration configuration;

    @BeforeMethod
    public void setUp() {
        BrowserSetup.Builder capability = BrowserSetup.build()
                .withType(BrowserType.FIREFOX)
                .withOS(BrowserOS.ANY);
        browser = UI.newBrowserWithImageRecognition(capability);
    }

    @Test
    @TestId(id = "TAF_UI_Func_13", title = "Basic functionality of Sikuli Client on Selenium Grid")
    public void testImageSearchRemotely() {
        BrowserTab browserTab = browser.getCurrentWindow();
        browserTab.maximize();
        browserTab.open(configuration.getString("loginUrl"));

        DesktopNavigator desktopClient = UI.newDesktopNavigator(browser, SAMPLE_IMAGES_BUNDLE);
        DesktopWindow currentWindow = desktopClient.getCurrentWindow();
        final ViewModel genericView = currentWindow.getGenericView();

        TextBox userNameField = genericView.getTextBox(USERNAME_FIELD);
        assertTrue(userNameField.exists(), "Username field not found");
        userNameField.setText("taf");

        TextBox passwordField = genericView.getTextBox(PASSWORD_FIELD);
        assertTrue(passwordField.exists(), "Password field not found");
        passwordField.setText("taf");

        UiComponent loginButton = genericView.getViewComponent(LOGIN_BUTTON);
        loginButton.click();

        browserTab.waitUntil(new GenericPredicate() {
            @Override
            public boolean apply() {
                UiComponent logoutButton = genericView.getViewComponent(LOGOUT_BUTTON);
                return logoutButton.exists();
            }
        }, 10 * 1000);

        genericView.getViewComponent(LOGOUT_BUTTON).click();
        genericView.getViewComponent(OK_BUTTON).click();
    }

    @Test(expectedExceptions = UiComponentNotFoundException.class)
    public void testImageSearch_shouldThrowUiComponentNotFoundException() throws Exception {
        DesktopNavigator desktopClient = UI.newDesktopNavigator(browser, SAMPLE_IMAGES_BUNDLE);
        DesktopWindow currentWindow = desktopClient.getCurrentWindow();
        final ViewModel genericView = currentWindow.getGenericView();

        TextBox searchField = genericView.getTextBox(USERNAME_FIELD);
        searchField.click();
    }
}

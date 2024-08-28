package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import com.ericsson.cifwk.taf.ui.BrowserCookie;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;

import java.util.Set;

/**
 * Created by ejambuc on 18/03/2015.
 */
public interface SampleWebAppOperator {

    void enterLoginDetails();
    String getCurrentUrl();
    void openLoginPage();
    Set<BrowserCookie> getCookies();
    void login();
    void login(BrowserType browserType);
    void saveMember();
    void deleteMemberRows(String... members);
    void navigateBack(String elementToWaitFor);
    void navigateForward(String elementToWaitFor);
    void enterMemberDetails();
    void refresh(String elementToWaitFor);
    boolean fieldsReset();
    void deleteMember();
    void dismissMessageBox(String buttonSelector);
    boolean isMessageBoxDisplayed();
    void importFile();
    boolean importSuccessful();
    int numberOfRecords();
    void showNumberOfRecords(int numberOfRecords);
    void copyAndPaste();
    boolean fieldsContainSameValue();
    void focusFirstNameField();
    boolean isFirstNameFieldFocused();
    void typeText(String text);
    String getFirstName();
    void navigateToAddMemberScreen();
    UiComponent getTable();
    UiComponent getTableBody();
    UiComponent getTableByXpath();
    void clickNextPageButton();
}

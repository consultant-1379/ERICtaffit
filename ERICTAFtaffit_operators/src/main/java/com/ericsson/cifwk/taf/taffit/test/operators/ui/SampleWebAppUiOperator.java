package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.openqa.selenium.Keys;

import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.taffit.test.components.MemberForm;
import com.ericsson.cifwk.taf.taffit.test.pages.AddUserScreenViewModel;
import com.ericsson.cifwk.taf.taffit.test.pages.SearchTableViewModel;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserCookie;
import com.ericsson.cifwk.taf.ui.BrowserOS;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.FileSelector;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.ericsson.cifwk.taf.ui.sdk.ViewModel;

/**
 * Created by ejambuc on 18/03/2015.
 */
@Operator
public class SampleWebAppUiOperator implements SampleWebAppOperator {

    private static final BrowserType DEFAULT_BROWSER_TYPE = BrowserType.FIREFOX;
    private BrowserType browserType = DEFAULT_BROWSER_TYPE;

    private Browser browser;

    private BrowserTab browserTab;

    private ViewModel genericViewModel;

    private AddUserScreenViewModel addUserScreenViewModel;

    private SearchTableViewModel searchTableViewModel;

    private MemberForm addMemberForm;

    @Inject
    private TafConfiguration configuration;

    @Override
    public void openLoginPage() {
        browser = UI.newBrowser(browserType, BrowserOS.LINUX);
        browserTab = browser.open(configuration.getString("loginUrl"));
        genericViewModel = browserTab.getGenericView();
    }

    @Override
    public void enterLoginDetails() {
        TextBox username = genericViewModel.getTextBox(SelectorType.XPATH, "//*[@id=\"Container\"]/div/div/form/div/input");
        TextBox password = genericViewModel.getTextBox(SelectorType.XPATH, "//*[@id=\"Container\"]/div/div/form/div/div[1]/input");
        Button enterPassword = genericViewModel.getButton(SelectorType.XPATH, "//*[@id=\"Container\"]/div/div/form/div/div[1]/button");
        username.setText("taf");
        password.setText("taf");
        enterPassword.click();
        addUserScreenViewModel = browserTab.getView(AddUserScreenViewModel.class);
        searchTableViewModel = browserTab.getView(SearchTableViewModel.class);
        addMemberForm = browserTab.getView(MemberForm.class);
        searchTableViewModel.getAddMemberButton().getText();
    }

    @Override
    public String getCurrentUrl() {
        return browserTab.getCurrentUrl();
    }

    @Override
    public void login() {
        openLoginPage();
        enterLoginDetails();
    }

    @Override
    public void login(BrowserType browserType) {
        this.browserType = browserType;
        login();
        this.browserType = DEFAULT_BROWSER_TYPE;
    }

    @Override
    public void saveMember() {
        Button createMember = addUserScreenViewModel.getCreateMember();
        createMember.click();
    }

    @Override
    public Set<BrowserCookie> getCookies() {
        login();
        return browserTab.getCookies();
    }

    @Override
    public void navigateToAddMemberScreen() {
        Button addMember = searchTableViewModel.getAddMemberButton();
        addMember.click();
    }

    private void setMemberValues() {
        addMemberForm.setName("john", "Smith");
        addMemberForm.setEmail("john.smith@ericsson.com");
        addMemberForm.setDescription("Admiral of New England");
        addMemberForm.setRole("Scrum Master");
        addMemberForm.setTeams("ENM", "Cinema");
        addMemberForm.setGender("Male");
        addMemberForm.setHasLaptop(true);
        addMemberForm.setHasAccess(true);
    }

    public void deleteMemberRows(String... surnames) {
        UiComponent memberTable = searchTableViewModel.getMemberTable();
        browserTab.waitUntilComponentIsDisplayed(SelectorType.CSS, ".elTablelib-CheckboxCell", 3000);
        List<UiComponent> members = memberTable.getDescendantsBySelector(".ebTableRow");
        browserTab.newActionChain()
                .keyDown(Keys.CONTROL).click(members.get(0)).click(members.get(1)).keyUp(Keys.CONTROL)
                .perform();
    }

    @Override
    public void navigateBack(String elementToWaitFor) {
        browserTab.back();
    }

    @Override
    public void navigateForward(String elementToWaitFor) {
        browserTab.forward();
    }

    @Override
    public void enterMemberDetails() {
        navigateToAddMemberScreen();
        setMemberValues();
    }

    @Override
    public void refresh(String elementToWaitFor) {
        browserTab.refreshPage();
        UI.pause(2000);
    }

    @Override
    public boolean fieldsReset() {
        TextBox firstName = addMemberForm.getFirstName();
        return firstName.getText().isEmpty();
    }

    @Override
    public void deleteMember() {
        searchTableViewModel.getDeleteMember().click();
    }

    @Override
    public void dismissMessageBox(String buttonSelector) {
        UiComponent messageBox = searchTableViewModel.getMessageBox();
        List<UiComponent> childElements = messageBox.getDescendantsBySelector(SelectorType.CSS, buttonSelector);
        childElements.get(0).click();
    }

    @Override
    public boolean isMessageBoxDisplayed() {
        return (searchTableViewModel.getMessageBox().isDisplayed());
    }

    @Override
    public void importFile() {
        FileSelector fileImport = searchTableViewModel.getImportFile();
        fileImport.select("DummyFilePath");
    }

    @Override
    public boolean importSuccessful() {
        UiComponent importNotification = searchTableViewModel.getImportSuccessNotification();
        return importNotification.isDisplayed();
    }

    @Override
    public int numberOfRecords() {
        UiComponent memberTable = searchTableViewModel.getMemberTable();
        List<UiComponent> members = memberTable.getDescendantsBySelector(".ebTableRow");
        return members.size();
    }

    @Override
    public void showNumberOfRecords(int numberOfRecords) {
        searchTableViewModel.getNumberOfRecordsDropdown().click();
        List<UiComponent> options = searchTableViewModel.getNumberOfRecordsOptions();
        options.get(2).click();
    }

    @Override
    public void copyAndPaste() {
        String selectAll = "a";
        String copy = "c";
        String paste = "v";
        browserTab.newActionChain()
                .click(addMemberForm.getFirstName()).keyDown(Keys.CONTROL)
                .sendKeys(selectAll).sendKeys(copy).click(addMemberForm.getSurname()).sendKeys(selectAll)
                .sendKeys(paste).keyUp(Keys.CONTROL)
                .perform();
    }

    @Override
    public boolean fieldsContainSameValue() {
        return addMemberForm.getFirstName().getText().equals(addMemberForm.getSurname().getText());
    }

    @Override
    public void focusFirstNameField(){
        addMemberForm.getFirstName().focus();
    }

    @Override
    public boolean isFirstNameFieldFocused() {
        return addMemberForm.getFirstName().hasFocus();
    }

    @Override
    public void typeText(String text) {
        browserTab.newActionChain().sendKeys(text).perform();
    }

    @Override
    public String getFirstName() {
        return addMemberForm.getFirstName().getText();
    }

    @Override
    public UiComponent getTable() {
        return searchTableViewModel.getTable();
    }

    @Override
    public UiComponent getTableBody() {
        return searchTableViewModel.getTableBody();
    }

    @Override
    public UiComponent getTableByXpath() {
        return searchTableViewModel.getTableByXpath();
    }

    @Override
    public void clickNextPageButton() {
        searchTableViewModel.clickNextPageButton();
    }
}

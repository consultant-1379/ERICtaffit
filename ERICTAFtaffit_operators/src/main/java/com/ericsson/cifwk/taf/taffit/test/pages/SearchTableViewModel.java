package com.ericsson.cifwk.taf.taffit.test.pages;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.FileSelector;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

import java.util.List;

/**
 * Created by ejambuc on 18/03/2015.
 */
public class SearchTableViewModel extends GenericViewModel {

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = "button.ebBtn.eb_wMargin.eaMembersApp-MembersActionBar-addMember")
    private Button addMember;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".elTablelib-Table-body")
    private UiComponent memberTable;

    @UiComponentMapping(selector = ".ebBtn.eb_wMargin.eaMembersApp-MembersActionBar-deleteMembers")
    private Button deleteMember;

    @UiComponentMapping(selector = ".ebDialogBox")
    private UiComponent messageBox;

    @UiComponentMapping(selector = ".ebBtn eb_wMargin.eaMembersApp-MembersActionBar-importFile")
    private FileSelector importFile;

    @UiComponentMapping(selector = "ebNotification-content")
    private UiComponent importSuccessNotification;

    @UiComponentMapping(selector = ".ebSelect.ebSelect_width_mini")
    private Button numberOfRecordsDropdown;

    @UiComponentMapping(selector = ".ebComponentList-item")
    private List<UiComponent> numberOfRecordsOptions;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".elTablelib-Table-table")
    private UiComponent table;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "/html/body/div[2]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/table")
    private UiComponent tableByXpath;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebPagination-nextAnchor")
    private UiComponent nextPageButton;

    public Button getAddMemberButton() {
        return addMember;
    }

    public UiComponent getMemberTable() {
        return memberTable;
    }

    public Button getDeleteMember() {
        return deleteMember;
    }

    public UiComponent getMessageBox() {
        return messageBox;
    }

    public FileSelector getImportFile() {
        return importFile;
    }

    public UiComponent getImportSuccessNotification() {
        return importSuccessNotification;
    }

    public Button getNumberOfRecordsDropdown() {
        return numberOfRecordsDropdown;
    }

    public List<UiComponent> getNumberOfRecordsOptions() {
        return numberOfRecordsOptions;
    }

    public UiComponent getTable() {
        return table;
    }

    public UiComponent getTableBody() {
        return memberTable;
    }

    public UiComponent getTableByXpath() {
        return tableByXpath;
    }

    public void clickNextPageButton() {
        nextPageButton.click();
    }
}

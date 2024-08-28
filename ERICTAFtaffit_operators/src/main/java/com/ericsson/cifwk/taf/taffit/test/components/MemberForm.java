package com.ericsson.cifwk.taf.taffit.test.components;

import com.ericsson.cifwk.taf.taffit.test.compositemodel.UIDropdownMenu;
import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.CheckBox;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.RadioButton;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ekiajen on 28/09/2015.
 */
public class MemberForm extends GenericViewModel {

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-name")
    private TextBox firstname;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-surname")
    private TextBox surname;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-email")
    private TextBox email;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-description")
    private TextBox description;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-roleSelect button")
    private Button roleButton;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebComponentList-item")
    private List<UiComponent> dropdownOptions;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-teamsMultiSelectHolder button")
    private Button teamsButton;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebRadioBtn")
    private List<UiComponent> genderRadiosHolder;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm-hasLaptopCheckbox .ebCheckbox")
    private CheckBox withLaptop;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebSwitcher-checkbox")
    private CheckBox hasAccessValue;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebSwitcher-switch")
    private Button hasAccess;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebComponentList.eb_scrollbar.elWidgets-ComponentList.ebComponentList_focus_forced")
    private UIDropdownMenu roleDropdown;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".ebComponentList.ebComponentList_focus_forced.eb_scrollbar")
    private UIDropdownMenu teamDropdown;



    Logger LOGGER = LoggerFactory.getLogger(MemberForm.class);

    public void setName(String firstName, String surname) {
        setFirstname(firstName);
        setSurname(surname);
    }

    public void setFirstname(String firstname) {
        this.firstname.setText(firstname);
    }

    public void setSurname(String surname) {
        this.surname.setText(surname);
    }

    public void setEmail(String email) {
        this.email.setText(email);
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

    public void setRole(String role) {
        roleButton.click();
        roleDropdown.clickOptionByName(role);
    }

    public void setGender(String gender) {
        switch (gender) {
            case "Male":
                genderRadiosHolder.get(0).click();
                break;
            case "Female":
                genderRadiosHolder.get(1).click();
                break;
            case "Unknown":
                genderRadiosHolder.get(2).click();
                break;
            default:
                LOGGER.error("Gender: " + gender + " did not match any choices so setting to 'Unknown'");
                genderRadiosHolder.get(3).click();
        }
    }

    public void setTeams(String... teams) {
        teamsButton.click();
        teamDropdown.clickDropboxOptionsByName(teams);
        teamsButton.click();
    }

    public void setHasLaptop(boolean hasLaptop) {
        if(hasLaptop) {
            withLaptop.select();
        }else {
            withLaptop.deselect();
        }
    }

    public void setHasAccess(boolean hasAccess) {
        boolean currentAccess = hasAccessValue.isSelected();
        if(hasAccess && !currentAccess) {
            this.hasAccess.click();
        }else if(!hasAccess && currentAccess) {
            this.hasAccess.click();
        }
    }

    public TextBox getFirstName() {
        return firstname;
    }

    public TextBox getSurname() {
        return surname;
    }
}

package com.ericsson.cifwk.taf.taffit.test.pages;

import com.ericsson.cifwk.taf.taffit.test.components.MemberForm;
import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

/**
 * Created by ejambuc on 18/03/2015.
 */
public class AddUserScreenViewModel extends GenericViewModel{

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-AddMemberActionBar-createLinkHolder")
    private Button createMember;

    @UiComponentMapping(selector = ".eaMembersApp-MemberForm-draggableElement")
    private UiComponent dragableElement;

    @UiComponentMapping(selector = ".eaMembersApp-MemberForm-droppableArea")
    private UiComponent dropableElement;

    @UiComponentMapping(selectorType = SelectorType.CSS, selector = ".eaMembersApp-MemberForm")
    private MemberForm addMemberForm;

    public TextBox getFirstName() {
        return addMemberForm.getFirstName();
    }

    public TextBox getSurname() {
        return addMemberForm.getSurname();
    }

    public Button getCreateMember() {
        return createMember;
    }

    public UiComponent getDragableElement() {
        return dragableElement;
    }

    public UiComponent getDropableElement() {
        return dropableElement;
    }

    public MemberForm getAddMemberForm() {
        return addMemberForm;
    }
}

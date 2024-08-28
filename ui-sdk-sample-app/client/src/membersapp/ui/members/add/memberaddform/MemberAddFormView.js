define([
    'jscore/core',
    'text!./_memberAddForm.html',
    'styles!./_memberAddForm.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.nameInput = element.find('.eaMembersApp-MemberForm-name');
            this.surnameInput = element.find('.eaMembersApp-MemberForm-surname');
            this.emailInput = element.find('.eaMembersApp-MemberForm-email');
            this.descriptionTextarea = element.find('.eaMembersApp-MemberForm-description');
            this.roleSelectHolder = element.find('.eaMembersApp-MemberForm-roleSelect');
            this.teamsMultiSelectHolder = element.find('.eaMembersApp-MemberForm-teamsMultiSelectHolder');
            this.hasAccessSwitcherHolder = element.find('.eaMembersApp-MemberForm-hasAccessSwitcher');
            this.hasLaptopCheckbox = element.find('.eaMembersApp-MemberForm-hasLaptopCheckbox > .ebCheckbox');
            this.genderRadioHolder = element.find('.eaMembersApp-MemberForm-genderRadiosHolder');
            this.draggableElement = element.find('.eaMembersApp-MemberForm-draggableElement');
            this.droppableArea = element.find('.eaMembersApp-MemberForm-droppableArea');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getNameInput: function () {
            return this.nameInput;
        },

        getSurnameInput: function () {
            return this.surnameInput;
        },

        getEmailInput: function () {
            return this.emailInput;
        },

        getDescriptionTextarea: function () {
            return this.descriptionTextarea;
        },

        getRoleSelectHolder: function () {
            return this.roleSelectHolder;
        },

        getTeamsMultiSelectHolder: function () {
            return this.teamsMultiSelectHolder;
        },

        getHasAccessSwitcherHolder: function () {
            return this.hasAccessSwitcherHolder;
        },

        getHasLaptopCheckbox: function () {
            return this.hasLaptopCheckbox;
        },

        getGenderValue: function () {
            var selectedRadio = this.genderRadioHolder.find('input:checked');
            if (!selectedRadio) {
                return '';
            }
            return selectedRadio.getProperty('value');
        },

        getDraggableElement: function () {
            return this.draggableElement;
        },

        getDroppableArea: function () {
            return this.droppableArea;
        }

    });

});

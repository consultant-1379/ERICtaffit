define([
    'jscore/core',
    'jscore/ext/binding',
    './MemberAddFormView',
    'widgets/SelectBox',
    'widgets/Switcher',
    '../../../../ext/MultiSelectBox'
], function (core, binding, View, SelectBox, Switcher, MultiSelectBox) {
    'use strict';



    return core.Widget.extend({
        /*jshint validthis:true*/

        View: View,

        GENDER_VALUES: {
            '': null,
            '1': {name: 'Male', value: 1},
            '2': {name: 'Female', value: 2},
            '3': {name: 'Unknown', value: 3}
        },

        init: function (options) {
            this.roleSelectBox = new SelectBox({
                items: [
                    {name: 'Lead Cook & Wan Fu', value: 1, title: 'Lead Cook & Wan Fu'},
                    {name: 'Samurai', value: 2, title: 'Samurai'},
                    {name: 'Worker', value: 3, title: 'Worker'},
                    {name: 'Artist', value: 4, title: 'Artist'},
                    {name: 'Architect', value: 5, title: 'Architect'},
                    {name: 'Web Developer', value: 6, title: 'Web Developer'},
                    {name: 'DB master', value: 7, title: 'DB master'},
                    {name: 'Software Developer', value: 8, title: 'Software Developer'},
                    {name: 'Lead Software Developer', value: 9, title: 'Lead Software Developer'},
                    {name: 'Scrum Master', value: 10, title: 'Scrum Master'}
                ],
                modifiers: [
                    {name: 'width', value: 'full'}
                ]
            });

            this.teamsSelectBox = new MultiSelectBox({
                items: [
                    {name: 'TAF', value: 1, title: 'TAF'},
                    {name: 'TAF UI', value: 2, title: 'TAF UI'},
                    {name: 'Test Registry', value: 3, title: 'Test Registry'},
                    {name: 'TMS', value: 4, title: 'TMS'},
                    {name: 'UI SDK', value: 5, title: 'UI SDK'},
                    {name: 'Ericsson Builder', value: 6, title: 'Ericsson Builder'},
                    {name: 'Cinema', value: 7, title: 'Cinema'},
                    {name: 'ENM', value: 8, title: 'ENM'}
                ],
                modifiers: [
                    {name: 'width', value: 'full'}
                ]
            });

            this.hasAccessSwitcher = new Switcher({
                value: false,
                onLabel: 'Yes',
                offLabel: 'No',
                offColor: 'red'
            });

            this.memberModel = options.memberModel;

            this.isItemDropped = false;
        },

        onViewReady: function () {
            this.view.afterRender();

            this.roleSelectBox.attachTo(this.view.getRoleSelectHolder());
            this.teamsSelectBox.attachTo(this.view.getTeamsMultiSelectHolder());
            this.hasAccessSwitcher.attachTo(this.view.getHasAccessSwitcherHolder());

            var droppableArea = this.view.getDroppableArea(),
                draggableElement = this.view.getDraggableElement();

            draggableElement.addEventHandler('dragstart', this.onDragStart, this);

            droppableArea.addEventHandler('dragenter', this.onDragEnter, this);
            droppableArea.addEventHandler('dragleave', this.onDragLeave, this);
            droppableArea.addEventHandler('dragover', this.onDragOver, this);
            droppableArea.addEventHandler('drop', this.onDrop, this);

            bindFields.call(this);
        },

        updateFormData: function () {
            this.memberModel.setRole(this.roleSelectBox.getValue());
            this.memberModel.setTeams(this.teamsSelectBox.getValue());
            this.memberModel.setHasLaptop(this.view.getHasLaptopCheckbox().getProperty('checked'));
            this.memberModel.setHasAccess(this.hasAccessSwitcher.getValue());

            var genderId = this.view.getGenderValue();
            this.memberModel.setGender(this.GENDER_VALUES[genderId]);
        },

        onDragStart: function (e) {
            e.originalEvent.dataTransfer.effectAllowed = 'copy';
            e.originalEvent.dataTransfer.setData('text/html', this.view.getDraggableElement()._getHTMLElement().innerHTML);
        },

        onDrop: function (e) {
            if (e.stopPropagation) {
                e.stopPropagation();
            }

            var elementText = e.originalEvent.dataTransfer.getData('text/html');
            if (elementText === 'Drop Me') {
                var droppableArea = this.view.getDroppableArea();
                droppableArea.setText('');
                droppableArea.append(this.view.getDraggableElement());
                droppableArea.removeModifier('over');
                this.isItemDropped = true;
            }
            return false;
        },

        onDragEnter: function () {
            this.view.getDroppableArea().setModifier('over');
            return false;
        },

        onDragLeave: function () {
            this.view.getDroppableArea().removeModifier('over');
        },

        onDragOver: function (e) {
            if (e.preventDefault) {
                e.preventDefault();
            }
            e.originalEvent.dataTransfer.effectAllowed = 'copy';
            return false;
        },

        getItemDropped: function () {
            return this.isItemDropped;
        }

    });

    function bindFields () {
        var mainModel = this.memberModel;

        binding.bindModel(mainModel, 'name', this.view.getNameInput(), 'value');
        binding.bindModel(mainModel, 'surname', this.view.getSurnameInput(), 'value');
        binding.bindModel(mainModel, 'email', this.view.getEmailInput(), 'value');
        binding.bindModel(mainModel, 'description', this.view.getDescriptionTextarea(), 'value');
    }

});

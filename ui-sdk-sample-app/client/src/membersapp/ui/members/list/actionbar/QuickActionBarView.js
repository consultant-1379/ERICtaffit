define([
    'jscore/core',
    'text!./_quickActionBar.html',
    'styles!./_quickActionBar.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.searchInput = element.find('.eaMembersApp-MembersActionBar-searchInput');
            this.searchButton = element.find('.eaMembersApp-MembersActionBar-searchButton');
            this.searchClearIcon = element.find('.eaMembersApp-MembersActionBar-searchClearIcon');
            this.addMember = element.find('.eaMembersApp-MembersActionBar-addMember');
            this.deleteMembers = element.find('.eaMembersApp-MembersActionBar-deleteMembers');
            this.importFile = element.find('.eaMembersApp-MembersActionBar-importFile');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getSearchInput: function () {
            return this.searchInput;
        },

        getSearchClearIcon: function () {
            return this.searchClearIcon;
        },

        getSearchButton: function () {
            return this.searchButton;
        },

        getAddMemberButton: function () {
            return this.addMember;
        },

        getDeleteMembersButton: function () {
            return this.deleteMembers;
        },

        getImportFileButton: function () {
            return this.importFile;
        }

    });

});

define([
    'jscore/core',
    'jscore/ext/utils/base/underscore',
    './QuickActionBarView',
    '../../../../common/widgets/actionIcon/ActionIcon',
    '../../../../common/Navigation',
    '../../../../ext/stringUtils'
], function (core, _, View, ActionIcon, Navigation, stringUtils) {
    'use strict';

    return core.Widget.extend({
        /*jshint validthis:true */

        View: View,

        init: function (options) {
            this.region = options.region;
            this.searchClearIcon = new ActionIcon({
                iconKey: 'close',
                interactive: true,
                hide: true
            });
        },

        onViewReady: function () {
            this.view.afterRender();

            this.view.getAddMemberButton().addEventHandler('click', function () {
                Navigation.navigateTo(Navigation.getAddMemberUrl());
            });

            this.searchClearIcon.attachTo(this.view.getSearchClearIcon());

            updateSearchClearIcon.call(this);

            this.view.getSearchButton().addEventHandler('click', this.onSearchButtonClick, this);
            this.view.getSearchInput().addEventHandler('keydown', this.onSearchInputKeyDown, this);
            this.view.getSearchInput().addEventHandler('input', updateSearchClearIcon, this);
            this.view.getSearchClearIcon().addEventHandler('click', this.onClearSearch, this);

            this.view.getDeleteMembersButton().addEventHandler('click', this.onMembersDeleteClick, this);
            this.view.getImportFileButton().addEventHandler('click', this.onImportFileClick, this);
        },

        onMembersDeleteClick: function (e) {
            e.preventDefault();
            this.region.removeMembersFromTable();
        },

        onImportFileClick: function (e) {
            e.preventDefault();
            this.region.showImportFileDialog();
        },

        onClearSearch: function (e) {
            e.preventDefault();
            this.onClearSearchInput();
            updateNavigation('');
        },

        onSearchButtonClick: function (e) {
            e.preventDefault();

            var searchInput = this.view.getSearchInput();

            var searchValue = stringUtils.trim(searchInput.getValue());
            searchInput.setValue(searchValue);
            updateNavigation(searchValue);
        },

        onSearchInputKeyDown: function (e) {
            var keyCode = e.originalEvent.keyCode || e.originalEvent.which;
            if (keyCode === 13) { // Enter
                this.view.getSearchButton().trigger('click');
                e.preventDefault();
            }
        },

        onClearSearchInput: function () {
            this.view.getSearchInput().setValue('');
            updateSearchClearIcon.call(this);
        }

    });

    function updateNavigation (searchValue) {
        if (!_.isEmpty(searchValue)) {
            searchValue = 'any~' + searchValue;
        }
        Navigation.navigateTo(Navigation.getDefaultUrlWithSearch(searchValue));
    }

    function updateSearchClearIcon () {
        var searchInput = this.view.getSearchInput();
        var isEmpty = searchInput.getValue().length === 0;
        this.searchClearIcon.setHidden(isEmpty);
    }

});

/*global location*/
define([
    'jscore/ext/utils/base/underscore',
    './Constants'
], function (_, Constants) {
    'use strict';

    var Navigation = {

        // Common functions
        navigateTo: function (url) {
            location.hash = url;
        },

        goToPreviousPage: function (e) {
            if (e) {
                e.preventDefault();
            }
            return history.back();
        },

        getDefaultUrl: function () {
            return Constants.urls.APP_NAME + '/' + Constants.urls.DEFAULT_PAGE;
        },

        getDefaultUrlWithSearch: function (searchValue) {
            var defaultUrl = Navigation.getDefaultUrl();
            if (_.isEmpty(searchValue)) {
                return defaultUrl;
            }
            return defaultUrl + '?q=' + searchValue;
        },

        getAddMemberUrl: function () {
            return Constants.urls.APP_NAME + '/' + Constants.urls.ADD_MEMBER_PAGE;
        }

    };

    return Navigation;

});

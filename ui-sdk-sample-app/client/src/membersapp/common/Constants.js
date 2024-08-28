define(function () {
    'use strict';

    var Constants = {};

    Constants.urls = {
        APP_NAME: '',
        DEFAULT_PAGE: 'list',
        ADD_MEMBER_PAGE: 'add'
    };

    Constants.pages = {
        MEMBERS_LIST_PAGE: 'membersListPage',
        ADD_MEMBER_PAGE: 'addMemberPage'
    };

    Constants.events = {
        SHOW_ERROR_BLOCK: 'showErrorBlock',
        HIDE_ERROR_BLOCK: 'hideErrorBlock',

        NOTIFICATION: 'notification'
    };

    return Constants;

});

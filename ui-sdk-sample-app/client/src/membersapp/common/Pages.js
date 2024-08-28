define([
    './Constants',
    '../ui/members/list/MembersRegion',
    '../ui/members/add/AddMemberRegion'
], function (Constants, MembersRegion, MemberAddRegion) {
    'use strict';

    return function () {
        this.pages = {};

        this.pages[Constants.pages.MEMBERS_LIST_PAGE] = {
            groupId: Constants.pages.MEMBERS_LIST_PAGE,
            isCreated: false,
            ContentRegion: MembersRegion
        };

        this.pages[Constants.pages.ADD_MEMBER_PAGE] = {
            groupId: Constants.pages.ADD_MEMBER_PAGE,
            isCreated: false,
            ContentRegion: MemberAddRegion
        };

    };

});

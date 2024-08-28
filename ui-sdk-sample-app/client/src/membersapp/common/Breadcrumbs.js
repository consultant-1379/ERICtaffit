define([
    './Navigation'
], function (Navigation) {
    'use strict';

    var Breadcrumbs = {

        getDefault: function () {
            return [
                {
                    name: 'Members',
                    url: '#' + Navigation.getDefaultUrl()
                }
            ];
        },

        getAddMember: function () {
            var breadcrumbs = Breadcrumbs.getDefault().slice(0);
            breadcrumbs.push({
                name: 'Add a Member',
                url: '#' + Navigation.getAddMemberUrl()
            });
            return breadcrumbs;
        }

    };

    return Breadcrumbs;

});

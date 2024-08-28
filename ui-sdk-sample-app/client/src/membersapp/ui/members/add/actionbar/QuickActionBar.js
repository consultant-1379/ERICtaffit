define([
    'jscore/core',
    './QuickActionBarView',
    '../../../../common/Navigation',
    '../../../../common/widgets/actionLink/ActionLink'
], function (core, View, Navigation, ActionLink) {
    'use strict';

    return core.Widget.extend({
        /*jshint validthis:true*/

        View: View,

        init: function (options) {
            this.region = options.region;
        },

        onViewReady: function () {
            this.view.afterRender();

            this.view.getBackButton().addEventHandler('click', Navigation.goToPreviousPage);

            initSaveMemberActionLink.call(this);
            initCancelActionLink.call(this);
        },

        onAddMemberLinkClick: function () {
            this.region.createMember();
        }

    });

    function initSaveMemberActionLink () {
        this.addToTestPlanActionLink = new ActionLink({
            icon: {iconKey: 'save', interactive: true, title: 'Create'},
            link: {text: 'Create'},
            action: this.onAddMemberLinkClick.bind(this)
        });
        this.addToTestPlanActionLink.attachTo(this.view.getCreateLinkHolder());
    }

    function initCancelActionLink () {
        this.addToTestPlanActionLink = new ActionLink({
            icon: {iconKey: 'undo', interactive: true, title: 'Cancel'},
            link: {text: 'Cancel'},
            action: Navigation.goToPreviousPage
        });
        this.addToTestPlanActionLink.attachTo(this.view.getCancelLinkHolder());
    }

});

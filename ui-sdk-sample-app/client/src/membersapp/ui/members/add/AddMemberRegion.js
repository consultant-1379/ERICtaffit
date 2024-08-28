define([
    '../../../common/region/PageRegion',
    '../../../common/Constants',
    '../../../common/Breadcrumbs',
    '../../../common/ModelHelper',
    '../../../common/Navigation',
    '../../../common/notifications/NotificationRegion/NotificationRegion',
    './AddMemberRegionView',
    './actionbar/QuickActionBar',
    './memberaddform/MemberAddForm',
    '../models/MemberModel'
], function (PageRegion, Constants, Breadcrumbs, ModelHelper, Navigation, NotificationRegion, View, ActionBar,
             MemberAddForm, MemberModel) {
    'use strict';

    return PageRegion.extend({
        /*jshint validthis:true*/

        View: View,

        init: function () {
            this.eventBus = this.getContext().eventBus;
            this.actionBar = new ActionBar({
                region: this
            });
            this.memberModel = new MemberModel();
            this.memberAddForm = new MemberAddForm({
                region: this,
                memberModel: this.memberModel
            });
        },

        onStart: function () {
            this.view.afterRender();

            this.view.getMemberFormHeading().setText('Add a Member');

            this.actionBar.attachTo(this.view.getActionBarHolder());
            this.memberAddForm.attachTo(this.view.getMemberForm());
        },

        getBreadcrumbs: function () {
            return Breadcrumbs.getAddMember();
        },

        createMember: function () {
            this.eventBus.publish(Constants.events.HIDE_ERROR_BLOCK);

            if (!this.memberAddForm.getItemDropped()) {
                sendWarningMessage.call(this);
                return;
            }

            this.memberAddForm.updateFormData();

            this.memberModel.save({}, {
                statusCode: ModelHelper.statusCodeHandler(this.eventBus, {
                    201: function () {
                        Navigation.goToPreviousPage();
                    }.bind(this)
                })
            });
        }

    });

    function sendWarningMessage() {
        var options = NotificationRegion.NOTIFICATION_TYPES.warning;
        options.canDismiss = true;
        options.canClose = true;
        var message = 'Before continue, please proceed with Drag&Drop area.';

        this.eventBus.publish(Constants.events.NOTIFICATION, message, options);
    }

});

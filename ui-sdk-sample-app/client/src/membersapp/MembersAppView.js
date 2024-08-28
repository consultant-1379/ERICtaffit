define([
    'jscore/core',
    'text!./_membersApp.html',
    'styles!./_membersApp.less',
    'styles!../cssBlockers/_assetsPatch.less'
], function (core, template, styles, assetsPatch) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.navigationHolder = element.find('.eaMembersApp-Navigation');
            this.notificationBlock = element.find('.eaMembersApp-NotificationBlock');
            this.contentHolder = element.find('.eaMembersApp-Content');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return assetsPatch + styles;
        },

        getNavigationHolder: function () {
            return this.navigationHolder;
        },

        getNotificationBlock: function () {
            return this.notificationBlock;
        },

        getContentHolder: function () {
            return this.contentHolder;
        }

    });

});

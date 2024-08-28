define([
    'jscore/core',
    './MembersAppView',
    './routing/AppRouter',
    './common/Constants',
    './common/Pages',
    './common/notifications/NotificationRegion/NotificationRegion'
], function (core, View, AppRouter, Constants, Pages, NotificationRegion) {
    'use strict';

    return core.App.extend({

        View: View,

        init: function (options) {
            this.namespace = options.namespace;
            Constants.urls.APP_NAME = this.namespace;
        },

        onStart: function () {
            this.view.afterRender();

            this.eventBus = this.getContext().eventBus;

            this.notificationRegion = new NotificationRegion({
                eventBus: this.eventBus
            });
            this.notificationRegion.start(this.view.getNotificationBlock());

            this.pagesObj = new Pages();
            this.router = new AppRouter(this.pagesObj.pages, this.getContext(), this.view.getContentHolder(), this.view.getNavigationHolder());
            this.router.start();
        },

        onStop: function () {
            this.router.stop();
        }

    });

});

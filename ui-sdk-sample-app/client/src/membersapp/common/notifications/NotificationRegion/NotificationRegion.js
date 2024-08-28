define([
    'jscore/core',
    'jscore/ext/utils/base/underscore',
    'widgets/Notification',
    './NotificationRegionView',
    '../../Constants'
], function (core, _, Notification, View, Constants) {
    'use strict';
    /* jshint validthis: true */

    var NotificationRegion = core.Region.extend({

        View: View,

        init: function (options) {
            this.eventBus = options.eventBus;
            this.messagesWidgets = [];
        },

        onStart: function () {
            this.view.afterRender();

            this.eventBus.subscribe(Constants.events.NOTIFICATION, this.notify, this);
            this.eventBus.subscribe(Constants.events.SHOW_ERROR_BLOCK, this.onShowErrorBlock, this);
            this.eventBus.subscribe(Constants.events.HIDE_ERROR_BLOCK, this.onHideErrorBlock, this);
        },

        notify: function (text, options) {
            options = options || {};
            var canClose = options.canClose != null ? options.canClose : true;
            var canDismiss = options.canDismiss != null ? options.canDismiss : false;
            var notification = new Notification({
                label: text,
                color: options.color,
                icon: options.icon,
                showCloseButton: canClose,
                showAsToast: false,
                autoDismiss: canDismiss
            });
            notification.attachTo(this.view.getHolder());
            this.messagesWidgets.push(notification);
            return notification;
        },

        onShowErrorBlock: function (params) {
            [].concat(params).forEach(function (messageObj) {
                var options = NotificationRegion.NOTIFICATION_TYPES.error;
                options.canDismiss = false;
                options.canClose = true;
                this.eventBus.publish(Constants.events.NOTIFICATION, messageObj.message, options);
            }.bind(this));
        },

        onHideErrorBlock: function () {
            this.messagesWidgets.forEach(function (messageWidget, index) {
                messageWidget.destroy();
                delete this.messagesWidgets[index];
            }.bind(this));
        }

    }, {
        NOTIFICATION_TYPES: {
            info: {color: 'paleBlue', icon: 'dialogInfo', canClose: true},
            success: {color: 'green', icon: 'tick', canClose: true},
            warning: {color: 'yellow', icon: 'warning', canClose: false},
            invalid: {color: 'red', icon: 'invalid', canClose: true},
            error: {color: 'red', icon: 'error', canClose: false}
        }
    });

    return NotificationRegion;

});

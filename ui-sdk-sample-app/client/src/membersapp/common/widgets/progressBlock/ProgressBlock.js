/*global define*/
define([
    'jscore/core',
    './ProgressBlockView',
    './message/Message'
], function (core, View, Message) {
    'use strict';

    return core.Widget.extend({
        /*jshint validthis:true */

        View: View,

        onViewReady: function () {
            this.view.afterRender();
        },

        showProgress: function () {
            this.view.getMessagesBlock().removeModifier('show');
            this.view.getProgressBlock().setModifier('show');
        },

        showMessages: function (messages) {
            detachMessages.call(this);

            messages.forEach(function (message) {
                var messageObj = new Message({message: message});
                messageObj.attachTo(this.view.getMessagesBlock());
            }, this);

            this.view.getMessagesBlock().setModifier('show');
            this.view.getProgressBlock().removeModifier('show');
        },

        hideProgress: function () {
            this.view.getProgressBlock().removeModifier('show');
        },

        hideMessages: function () {
            detachMessages.call(this);
            this.view.getMessagesBlock().removeModifier('show');
        }

    });

    function detachMessages () {
        var children = this.view.getMessagesBlock().children();
        if (children.length > 0) {
            children.forEach(function (child) {
                child.detach();
            });
        }
    }

});

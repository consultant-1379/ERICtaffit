define([
    'jscore/core',
    'text!./NotificationRegion.html'
], function (core, template) {
    'use strict';

    return core.View.extend({

        getTemplate: function () {
            return template;
        },

        afterRender: function () {
            this.holder = this.getElement();
        },

        getHolder: function () {
            return this.holder;
        }

    });

});

define([
    'jscore/core',
    'text!./ButtonCell.html'
], function (core, template) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            this.button = this.getElement().find('.eaMembersApp-ButtonCell-button');
        },

        getTemplate: function () {
            return template;
        },

        getButton: function () {
            return this.button;
        }

    });

});

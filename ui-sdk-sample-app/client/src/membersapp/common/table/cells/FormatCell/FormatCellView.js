define([
    'jscore/core',
    'text!./FormatCell.html'
], function (core, template) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            this.label = this.getElement().find('.eaMembersApp-FormatCell-label');
        },

        getTemplate: function () {
            return template;
        },

        getLabel: function () {
            return this.label;
        }

    });

});

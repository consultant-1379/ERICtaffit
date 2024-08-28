define([
    'jscore/core',
    'text!./LinkCell.html'
], function (core, template) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            this.link = this.getElement().find('.eaMembersApp-LinkCell-link');
        },

        getTemplate: function () {
            return template;
        },

        getLink: function () {
            return this.link;
        }

    });

});

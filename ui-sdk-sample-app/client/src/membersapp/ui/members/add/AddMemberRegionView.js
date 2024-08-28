define([
    'jscore/core',
    'text!./_addMember.html',
    'styles!./_addMember.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.actionBarHolder = element.find('.ebQuickActionBar');
            this.memberFormHeading = element.find('.eaMembersApp-AddMember-content h2');
            this.memberForm = element.find('.eaMembersApp-AddMember-form');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getActionBarHolder: function () {
            return this.actionBarHolder;
        },

        getMemberFormHeading: function () {
            return this.memberFormHeading;
        },

        getMemberForm: function () {
            return this.memberForm;
        }

    });

});

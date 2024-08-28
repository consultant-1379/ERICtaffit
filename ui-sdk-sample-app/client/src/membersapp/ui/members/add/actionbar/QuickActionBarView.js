define([
    'jscore/core',
    'text!./_quickActionBar.html',
    'styles!./_quickActionBar.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.backButton = element.find('.ebQuickActionBar-back');
            this.createLinkHolder = element.find('.eaMembersApp-AddMemberActionBar-createLinkHolder');
            this.cancelLinkHolder = element.find('.eaMembersApp-AddMemberActionBar-cancelLinkHolder');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getBackButton: function () {
            return this.backButton;
        },

        getCreateLinkHolder: function () {
            return this.createLinkHolder;
        },

        getCancelLinkHolder: function () {
            return this.cancelLinkHolder;
        }

    });

});

define([
    'jscore/core',
    'text!./_members.html',
    'styles!./_members.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.actionBarHolder = element.find('.ebQuickActionBar');
            this.tableHolder = element.find('.eaMembersApp-MembersList-table');
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

        getTableHolder: function () {
            return this.tableHolder;
        }

    });

});

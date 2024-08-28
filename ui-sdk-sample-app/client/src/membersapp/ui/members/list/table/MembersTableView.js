define([
    'jscore/core',
    'text!./_membersTable.html',
    'styles!./_membersTable.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.tableHolder = element.find('.eaMembersApp-MembersTable-tableHolder');
            this.itemsCount = element.find('.eaMembersApp-MembersTable-count');
            this.headingCommands = element.find('.ebLayout-HeadingCommands-block');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getTableHolder: function () {
            return this.tableHolder;
        },

        getItemsCount: function () {
            return this.itemsCount;
        },

        getHeadingCommands: function () {
            return this.headingCommands;
        }

    });

});

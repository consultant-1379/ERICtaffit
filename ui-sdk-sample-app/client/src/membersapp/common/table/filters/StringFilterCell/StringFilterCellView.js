define([
    'jscore/core',
    'text!./StringFilterCell.html',
    'styles!./StringFilterCell.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.inputBox = element.find('.eaMembersApp-StringFilter-input');
            this.optionsElement = element.find('.eaMembersApp-StringFilter-options');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getInputBox: function () {
            return this.inputBox;
        },

        getOptionsBlock: function () {
            return this.optionsElement;
        }

    });

});

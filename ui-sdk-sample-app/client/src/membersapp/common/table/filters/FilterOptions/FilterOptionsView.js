define([
    'jscore/core',
    'text!./FilterOptions.html',
    'styles!./FilterOptions.less'
], function (core, template, style) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            this.buttonElement = this.getElement();
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return style;
        },

        setSelectedName: function (value) {
            this.buttonElement.setText(value);
        },

        getSelectedName: function () {
            return this.buttonElement.getText();
        }

    });

});

/*global define*/
define([
    'jscore/core',
    'text!./_fileImportWidget.html',
    'styles!./_fileImportWidget.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        afterRender: function () {
            var element = this.getElement();
            this.inputFile = element.find('.eaMembersApp-ImportFileForm-input');
        },

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        },

        getInputFile: function () {
            return this.inputFile;
        }

    });

});

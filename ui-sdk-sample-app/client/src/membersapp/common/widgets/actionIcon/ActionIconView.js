/*global define*/
define([
    'jscore/core',
    'text!./ActionIcon.html',
    'styles!./_actionIcon.less'
], function (core, template, styles) {
    'use strict';

    return core.View.extend({

        getTemplate: function () {
            return template;
        },

        getStyle: function () {
            return styles;
        }

    });

});

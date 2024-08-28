define([
    'jscore/core',
    'jscore/ext/utils/base/underscore',
    'widgets/table/Cell',
    './FormatCellView'
], function (core, _, Cell, View) {
    'use strict';

    return function (options) {
        options.mapper = options.mapper || function (value) {
            return value;
        };

        return Cell.extend({

            View: View,

            setValue: function (value) {
                this.value = options.mapper.call(null, value);
                this.view.getLabel().setText(this.value);
            },

            getValue: function () {
                return this.value;
            },

            setTooltip: function () {
                this.view.getElement().setAttribute('title', this.value);
            }

        });
    };

});

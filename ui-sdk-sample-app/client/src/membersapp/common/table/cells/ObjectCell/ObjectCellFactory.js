define([
    'widgets/table/Cell'
], function (Cell) {
    'use strict';

    return function (options) {

        return Cell.extend({

            init: function () {
                this.field = options.field;
                if (!this.field) {
                    throw new Error('Field should be defined for ObjectCell');
                }
            },

            setValue: function (valueObj) {
                this.valueTitle = valueObj[this.field];
                this.getElement().setText(this.valueTitle);
                this.getElement().setAttribute('title', this.valueTitle);
            },

            getValue: function () {
                return this.valueTitle;
            },

            setTooltip: function () {
                this.view.getElement().setAttribute('title', this.valueTitle);
            }

        });
    };

});

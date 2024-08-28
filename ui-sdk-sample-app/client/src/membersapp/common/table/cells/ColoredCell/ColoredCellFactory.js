define([
    'widgets/table/Cell'
], function (Cell) {
    'use strict';

    return function (options) {

        return Cell.extend({

            init: function () {
                this.replaceMap = options.replaceMap || {};
                this.defaultValue = options.defaultValue;
            },

            setValue: function (valueObj) {
                this.valueId = valueObj !== null ? valueObj.id : '';
                this.valueTitle = valueObj !== null ? valueObj.title : this.defaultValue.title;

                var color = this.defaultValue.color;
                if (this.replaceMap.hasOwnProperty(this.valueId)) {
                    var replaceObj = this.replaceMap[this.valueId];
                    color = replaceObj.color;
                }

                this.getElement().setAttribute('title', this.valueTitle);
                this.getElement().setStyle('background-color', color);
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

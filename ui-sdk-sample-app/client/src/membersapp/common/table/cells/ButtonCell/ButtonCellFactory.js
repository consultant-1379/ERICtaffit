define([
    'jscore/core',
    'jscore/ext/utils/base/underscore',
    'widgets/table/Cell',
    './ButtonCellView'
], function (core, _, Cell, View) {
    'use strict';

    return function (options) {

        return Cell.extend({

            View: View,

            onCellReady: function () {
                var button = this.view.getButton();
                button.setText(options.button.label);
                button.setAttribute('title', options.button.title);

                if (options.button.color) {
                    button.setModifier('color', options.button.color);
                }

                button.addEventHandler('click', function (e) {
                    e.preventDefault();
                    options.button.action.call(null, this.options.row.getData(), e);
                }, this);
            }
        });
    };
});

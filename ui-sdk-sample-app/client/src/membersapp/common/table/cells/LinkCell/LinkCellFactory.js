define([
    'jscore/ext/utils/base/underscore',
    'widgets/table/Cell',
    './LinkCellView'
], function (_, Cell, View) {
    'use strict';

    return function (options) {

        return Cell.extend({

            View: View,

            onCellReady: function () {
                if (options.url) {
                    if (_.isFunction(options.url)) {
                        this.view.getLink().setAttribute('href', options.url.call(null, this.options.row.getData()));
                    } else {
                        this.view.getLink().setAttribute('href', options.url);
                    }
                } else if (options.action) {
                    this.view.getLink().addEventHandler('click', function (e) {
                        e.preventDefault();
                        options.action.call(null, this.options.row.getData(), e);
                    }, this);
                }
            },

            setValue: function (value) {
                this.value = value;
                this.view.getLink().setText(value);
            },

            getValue: function () {
                return this.value;
            }

        });
    };

});

/*global define*/
define([
    'jscore/core'
], function (core) {
    'use strict';

    return core.Widget.extend({

        onViewReady: function () {
            this.getElement().setText(this.options.message);
        }

    });

});

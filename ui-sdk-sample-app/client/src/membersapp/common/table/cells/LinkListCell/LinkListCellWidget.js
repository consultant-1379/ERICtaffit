define([
    'jscore/core',
    './LinkListCellView'
], function (core, View) {
    'use strict';

    return core.Widget.extend({

        view: function () {
            return new View(this.options);
        }
    });
});

define([
    '../Plugin'
], function (Plugin) {
    'use strict';

    return Plugin.extend({
        /*jshint validthis:true */

        injections: {

            after: {
                addRow: addRow
            }

        }

    });

    function addRow (obj, index) {
        var table = this.getTable(),
            rowIdentifier = index !== undefined ? index : table.getRows().length - 1,
            row = table.getRows()[rowIdentifier];

        if (this.options.rowIdentifier) {
            rowIdentifier = obj[this.options.rowIdentifier];
        }
        row.getElement().setAttribute('data-id', rowIdentifier);
    }

});

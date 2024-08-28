define([
    'jscore/ext/utils/base/underscore',
    '../Plugin'
], function (_, Plugin) {
    'use strict';

    return Plugin.extend({
        /*jshint validthis:true */

        injections: {

            after: {
                onViewReady: onViewReady
            }

        }

    });

    function onViewReady () {
        var headerCells = this.getTable().getHeaderRow().getCells(),
            headerClasses = this.options.headerClasses;
        var cellIndexes = _.keys(headerClasses);
        cellIndexes.forEach(function (cellIndex) {
            var headerCell = headerCells[cellIndex];
            headerCell.getElement().setAttribute('class', headerClasses[cellIndex]);
        });
    }

});

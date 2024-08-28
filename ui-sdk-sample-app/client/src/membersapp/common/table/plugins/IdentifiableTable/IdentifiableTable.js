define([
    '../Plugin'
], function (Plugin) {
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
        this.getTable().view.getTable().setAttribute(this.options.attribute, this.options.identifier);
    }

});

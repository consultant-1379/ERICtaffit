define([
    'jscore/core',
    'template!./LinkListCell.html'
], function (core, template) {
    'use strict';

    return core.View.extend({

        getTemplate: function () {
            return template(this.options);
        }
    });
});

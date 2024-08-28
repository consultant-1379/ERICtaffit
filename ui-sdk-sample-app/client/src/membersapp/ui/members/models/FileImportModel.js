define([
    'jscore/ext/mvp'
], function (mvp) {
    'use strict';

    return mvp.Model.extend({

        url: '/api/files',

        getId: function () {
            return this.getAttribute('id');
        },

        getName: function () {
            return this.getAttribute('name');
        }

    });

});

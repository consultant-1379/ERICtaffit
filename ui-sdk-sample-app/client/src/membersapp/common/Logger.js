define(function () {
    /* global console: true */
    'use strict';

    var Logger = function (owner) {
        this.owner = owner;
    };

    var prefix = function (owner) {
        return '[' + owner + ']';
    };

    Logger.prototype = {

        info: function (msg) {
            console.log(prefix(this.owner), msg);
        },

        warn: function (msg) {
            console.warn(prefix(this.owner), msg);
        },

        error: function (msg) {
            console.error(prefix(this.owner), msg);
        }

    };

    return Logger;
});

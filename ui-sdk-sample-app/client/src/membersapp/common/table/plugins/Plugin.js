define([
    'jscore/core'
], function (core) {
    'use strict';

    var Plugin = function (options) {
        this.options = options || {};
        this.init.apply(this, arguments);
    };

    Plugin.prototype.init = function () {
    };
    Plugin.prototype.getOptions = function () {
        return this.options;
    };

    Plugin.prototype.getTable = function () {
        return this._table;
    };
    Plugin.prototype.injections = {
        before: {},
        after: {},
        newMethods: {}
    };

    Plugin.extend = core.extend;

    return Plugin;

});

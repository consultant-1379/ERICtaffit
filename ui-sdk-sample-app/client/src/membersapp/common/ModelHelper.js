define([
    'jscore/ext/utils/base/underscore',
    './Logger',
    './Constants'
], function (_, Logger, Constants) {
    'use strict';

    var logger = new Logger('ModelHelper');

    var ModelHelper = {};

    ModelHelper.authenticationHandler = function (eventBus, codes) {
        codes = codes || {};
        return _.extend({
            401: function () {
                eventBus.publish(Constants.events.AUTHENTICATION_REQUIRED);
            }
        }, codes);
    };

    ModelHelper.statusCodeHandler = function (eventBus, codes, df) {
        codes = codes || {};
        return _.extend({
            200: function () {
                if (df != null) {
                    df.resolve();
                }
            },
            400: function (data) {
                eventBus.publish(Constants.events.SHOW_ERROR_BLOCK, data.responseJSON);
                if (df != null) {
                    df.reject(data.responseJSON);
                }
            },
            401: function () {
                eventBus.publish(Constants.events.AUTHENTICATION_REQUIRED);
                if (df != null) {
                    df.reject();
                }
            },
            404: function (data) {
                var arg = [{
                    message: 'Some errors appeared on server when saving data.',
                    developerMessage: data
                }];
                eventBus.publish(Constants.events.SHOW_ERROR_BLOCK, arg);
                if (df != null) {
                    df.reject(data.responseJSON);
                }
            },
            500: function (data) {
                logger.error(data);
                var arg = [{
                    message: 'Some errors appeared on server when saving data.',
                    developerMessage: data
                }];
                eventBus.publish(Constants.events.SHOW_ERROR_BLOCK, arg);
                if (df != null) {
                    df.reject(arg);
                }
            }
        }, codes);
    };

    return ModelHelper;
});

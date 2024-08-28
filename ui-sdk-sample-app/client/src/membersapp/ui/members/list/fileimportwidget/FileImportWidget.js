/*global FormData*/
define([
    'jscore/core',
    'jscore/ext/net',
    './FileImportWidgetView'
], function (core, net, View) {
    'use strict';

    var FileImportWidget = core.Widget.extend({

        View: View,

        onViewReady: function () {
            this.view.afterRender();
        },

        clearInput: function () {
            this.view.getInputFile().setValue('');
        },

        submitForm: function () {
            var data = new FormData();
            data.append('fileimport', this.view.getInputFile().getProperty('files')[0]);

            net.ajax({
                url: '/api/files',
                type: 'POST',
                dataType: 'json',
                contentType: false,
                processData: false,
                data: data,

                statusCode: {
                    201: function (data) {
                        this.trigger(FileImportWidget.FILE_SENT, data);
                    }.bind(this),
                    400: function (data) {
                        this.trigger(FileImportWidget.FILE_SENT_ERROR, data.responseJSON);
                    }.bind(this),
                    401: function () {
                        this.trigger(FileImportWidget.AUTHENTICATION_REQUIRED);
                    }.bind(this),
                    404: function (data) {
                        this.trigger(FileImportWidget.FILE_SENT_ERROR, data.responseJSON);
                    }.bind(this),
                    500: function (data) {
                        var response = {
                            message: 'Some errors appeared on server trying to upload a file.',
                            developerMessage: data
                        };
                        this.trigger(FileImportWidget.FILE_SENT_ERROR, response);
                    }.bind(this)
                },

                error: function (data) {
                    this.trigger(FileImportWidget.FILE_SENT_ERROR, data);
                }.bind(this)
            });
        }

    }, {
        FILE_SENT: 'fileSent',
        FILE_SENT_ERROR: 'fileSendError',
        AUTHENTICATION_REQUIRED: 'authenticationRequired'
    });

    return FileImportWidget;

});

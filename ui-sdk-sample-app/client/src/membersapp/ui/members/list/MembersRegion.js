define([
    'jscore/ext/utils/base/underscore',
    '../../../common/region/PageRegion',
    '../../../common/Constants',
    '../../../common/Breadcrumbs',
    '../../../common/ModelHelper',
    '../../../common/Logger',
    './MembersRegionView',
    'widgets/Dialog',
    './actionbar/QuickActionBar',
    './table/MembersTable',
    './fileimportwidget/FileImportWidget',
    '../../../common/widgets/progressBlock/ProgressBlock',
    '../../../common/notifications/NotificationRegion/NotificationRegion',
    '../models/MembersCollection',
    '../models/FileImportModel'
], function (_, PageRegion, Constants, Breadcrumbs, ModelHelper, Logger, View, Dialog, ActionBar, MembersTable,
             FileImportWidget, ProgressBlock, NotificationRegion, MembersCollection, FileImportModel) {
    'use strict';

    var logger = new Logger('TestCaseSearchBarRegion');

    return PageRegion.extend({
        /*jshint validthis:true*/

        View: View,

        init: function () {
            this.eventBus = this.getContext().eventBus;
            this.actionBar = new ActionBar({
                region: this
            });
            this.membersCollection = new MembersCollection();
            this.membersTableWidget = new MembersTable({
                region: this,
                membersCollection: this.membersCollection
            });
        },

        onStart: function () {
            this.view.afterRender();

            this.actionBar.attachTo(this.view.getActionBarHolder());
            this.membersTableWidget.attachTo(this.view.getTableHolder());

            this.membersCollection.fetch({reset: true});
        },

        onShow: function (queryObj) {
            this.eventBus.publish(Constants.events.HIDE_ERROR_BLOCK);
            displayMembers.call(this, queryObj);
        },

        onRedraw: function (attributesObj, queryObj) {
            displayMembers.call(this, attributesObj, queryObj);
        },

        getEventBus: function () {
            return this.eventBus;
        },

        updateSelection: function (selectedItems) {
            this.membersCollection.each(function (model) {
                var testCaseId = model.getId();
                var foundItem = _.find(selectedItems, function (selectedItem) {
                    var selectedTestCaseId = selectedItem.options.model.id;
                    return testCaseId === selectedTestCaseId;
                });
                model.setAttribute('selected', foundItem !== undefined);
            });
        },

        removeMembersFromTable: function () {
            var selectedMembers = listOfSelectedMembers(this.membersCollection);

            if (selectedMembers.length) {
                showRemoveMembersDialog.call(this, selectedMembers);
            } else {
                showSelectMembersWarningDialog();
            }
        },

        showImportFileDialog: function () {
            if (!this.fileImportForm) {
                this.fileImportForm = new FileImportWidget();
                this.fileImportFormProgress = new ProgressBlock();

                this.fileImportWindow = new Dialog({
                    header: 'Import File',
                    content: this.fileImportForm,
                    optionalContent: this.fileImportFormProgress,
                    buttons: [
                        {caption: 'Import', color: 'green', action: function () {
                            submitFileImportForm.call(this);
                        }.bind(this)},
                        {caption: 'Cancel', action: function () {
                            closeFileImportDialog.call(this);
                        }.bind(this)}
                    ]
                });

                this.fileImportForm.addEventHandler(FileImportWidget.FILE_SENT, fileSentSuccessfully.bind(this));
                this.fileImportForm.addEventHandler(FileImportWidget.FILE_SENT_ERROR, fileSendError.bind(this));
            }

            this.fileImportWindow.show();
        },

        getBreadcrumbs: function () {
            return Breadcrumbs.getDefault();
        }

    });

    function displayMembers (queryObj) {
        this.membersCollection.resetPage();
        this.membersCollection.setSearchQuery('');
        if (queryObj && queryObj.hasOwnProperty('q')) {
            this.membersCollection.setSearchQuery(queryObj.q);
        }
        this.membersCollection.fetch({reset: true});
    }

    function listOfSelectedMembers (membersCollection) {
        var selected = [];
        membersCollection.each(function (model) {
            var checked = model.getAttribute('selected');
            if (checked) {
                selected.push(model);
            }
        });
        return selected;
    }

    function showRemoveMembersDialog (membersToRemove) {
        var membersToRemoveString = membersToRemove.map(function (itemModel) {
            return itemModel.getName() + ' ' + itemModel.getSurname();
        }).join('; ');

        var dialog = new Dialog({
            header: 'Remove users',
            type: 'information',
            content: 'Are you sure you want to remove these members: [' + membersToRemoveString + ']',
            buttons: [
                {
                    caption: 'Remove',
                    color: 'green',
                    action: function () {
                        membersToRemove.forEach(function (modelToRemove) {
                            this.membersCollection.removeModel(modelToRemove);
                        }, this);
                        this.membersTableWidget.refreshTable();
                        dialog.hide();
                    }.bind(this)
                },
                {
                    caption: 'Cancel',
                    action: function () {
                        this.membersTableWidget.deselectAll();
                        dialog.hide();
                    }.bind(this)
                }
            ]
        });
        dialog.show();
    }

    function showSelectMembersWarningDialog () {
        var warningDialog = new Dialog({
            header: 'Select Members',
            content: 'Please select Members to remove',
            type: 'information',
            buttons: [
                {
                    caption: 'OK',
                    color: 'blue',
                    action: function () {
                        warningDialog.hide();
                    }
                }
            ]
        });
        warningDialog.show();
    }

    function closeFileImportDialog () {
        this.fileImportWindow.hide();
        this.fileImportForm.clearInput();
        this.fileImportFormProgress.hideMessages();
    }

    function submitFileImportForm () {
        this.fileImportForm.submitForm();
        this.fileImportFormProgress.showProgress();
    }

    function fileSentSuccessfully (params) {
        var importId = params.id;

        var model = new FileImportModel();
        model.set('id', importId);

        model.fetch({
            success: function (model) {
                closeFileImportDialog.call(this);
                this.fileImportFormProgress.hideProgress();

                var options = NotificationRegion.NOTIFICATION_TYPES.success;
                options.canDismiss = true;
                options.canClose = true;
                var message = 'File "' + model.getName() + '" was successfully uploaded.';

                this.eventBus.publish(Constants.events.NOTIFICATION, message, options);
            }.bind(this),
            statusCode: ModelHelper.authenticationHandler(this.eventBus)
        });
    }

    function fileSendError (params) {
        var messages = [];

        if (typeof params.message === 'string') {
            messages.push(params.message);
            logger.warn(params.developerMessage);
        } else if (params.message instanceof Array) {
            messages = params.message;
        } else {
            messages.push('Some errors appeared trying to upload a file.');
        }

        this.fileImportFormProgress.showMessages(messages);
    }

});

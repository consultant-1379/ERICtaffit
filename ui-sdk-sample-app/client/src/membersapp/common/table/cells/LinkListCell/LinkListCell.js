/*global define*/
define([
    'widgets/table/Cell',
    './LinkListCellWidget',
    '../../Constants'
], function (Cell, LinkListCellWidget, Constants) {
    'use strict';

    var LinkListCell = Cell.extend({
        /*jshint validthis:true */

        setValue: function (data) {
            this.data = data;
            this.dataArray = addPermanentUrlToData.call(this);

            var linkData = sliceLastRequirement.call(this);
            var linkList = new LinkListCellWidget({
                requirements: linkData.first,
                lastRequirement: linkData.last.pop()
            });
            linkList.attachTo(this.getElement());
        },

        getValue: function () {
            return this.data;
        },

        setTooltip: function () {
            this.view.getElement().setAttribute('title', getItemsStr.call(this));
        }

    }, {
        NO_ITEMS_MESSAGE: 'No items added.'
    });

    return LinkListCell;

    function getItemsStr () {
        if (this.data && this.data.length > 0) {
            return this.data.join(', ');
        }
        return LinkListCell.NO_ITEMS_MESSAGE;
    }

    function sliceLastRequirement () {
        if (this.dataArray) {
            if (this.dataArray.length > 1) {
                var first = this.dataArray.slice(0, this.dataArray.length - 1);
                var second = this.dataArray.slice(this.dataArray.length - 1);
                return {first: first, last: second};
            } else {
                return {last: this.dataArray};
            }
        }
        return {};
    }

    function addPermanentUrlToData () {
        var dataArray = [];
        if (this.data) {
            this.data.forEach(function (entry) {
                dataArray.push({
                    permanentUrl: Constants.urls.JIRA_BROWSE_LINK,
                    data: entry
                });
            });
        }
        return dataArray;
    }

});

define([
    'jscore/ext/utils/base/underscore',
    'tablelib/Cell',
    './StringFilterCellView',
    '../FilterOptions/FilterOptions'
], function (_, Cell, View, FilterOptions) {
    'use strict';

    return Cell.extend({
        /*jshint validthis:true */

        View: View,

        init: function () {
            sendFilterEvent = _.debounce(sendFilterEvent, 300);
            this.attribute = this.getColumnDefinition().attribute;
        },

        onViewReady: function () {
            this.view.afterRender();

            this.view.getInputBox().addEventHandler('input', sendFilterEvent.bind(this));

            // Filter options will provide us the dropdown with our comparator operations
            this.filterOptions = new FilterOptions();
            this.filterOptions.attachTo(this.view.getOptionsBlock());
            this.filterOptions.addEventHandler('change', sendFilterEvent.bind(this));

            var defaultFilter = this.filterOptions.getDefaultValue();
            this.filterInfo = createFilterInfo(this.attribute, '', defaultFilter.value);
        },

        setValue: function () {
            // Value for this field is not needed
        }

    });

    function sendFilterEvent () {
        var selectedFilterObj = createFilterInfo(this.attribute, this.view.getInputBox().getValue(), this.filterOptions.getValue());
        if (_.isEqual(this.filterInfo, selectedFilterObj)) {
            return;
        }
        this.getTable().trigger('filter', selectedFilterObj.attribute, selectedFilterObj.value, selectedFilterObj.comparator);
        this.filterInfo = selectedFilterObj;
    }

    function createFilterInfo (attribute, value, comparator) {
        return {
            value: value,
            comparator: comparator,
            attribute: attribute
        };
    }

});

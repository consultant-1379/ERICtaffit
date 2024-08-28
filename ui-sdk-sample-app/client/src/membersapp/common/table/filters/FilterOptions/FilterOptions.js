define([
    'widgets/ItemsControl',
    './FilterOptionsView'
], function (ItemsControl, View) {
    'use strict';

    return ItemsControl.extend({

        View: View,

        init: function () {
            this.filterItems = this.options.items || [
                {value: '=', name: '=', title: 'equals'},
                {value: '!=', name: '!=', title: 'not equals'},
                {value: '~', name: '~', title: 'contains'}
            ];
        },

        onControlReady: function () {
            this.view.afterRender();

            this.setItems(this.filterItems);

            // Set the default option
            this.view.setSelectedName(this.filterItems[0].name);
        },

        onItemSelected: function (selectedValue) {
            this.view.setSelectedName(selectedValue.name);
            this.trigger('change');
        },

        getValue: function () {
            return this.view.getSelectedName();
        },

        getDefaultValue: function () {
            if (this.filterItems.length < 1) {
                throw new Error ('FilterOptions should have at least one item.');
            }
            return this.filterItems[0];
        }

    });

});

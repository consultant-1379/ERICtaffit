/*global define*/
define([
    'widgets/MultiSelectBox'
], function (MultiSelectBox) {
    'use strict';

    return MultiSelectBox.extend({

        setValues: function (values) {
            if (this.componentList) {
                this.componentList.view.getItems().forEach(function (itemObj, index) {
                    var isSelected = false,
                        checkbox = itemObj.find('.ebCheckbox'),
                        checkboxValue = checkbox.getValue();

                    values.forEach(function (valueObj) {
                        if (checkboxValue === valueObj.value) {
                            isSelected = true;
                        }
                    });

                    checkbox.setProperty('checked', isSelected);
                    this.componentList.onListItemClicked(index, checkbox);
                }.bind(this));
            }
        }

    });

});

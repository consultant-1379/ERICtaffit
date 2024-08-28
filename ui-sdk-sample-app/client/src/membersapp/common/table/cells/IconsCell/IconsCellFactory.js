define([
    'widgets/table/Cell',
    './IconsCellView',
    '../../../widgets/actionIcon/ActionIcon'
], function (Cell, View, ActionIcon) {
    'use strict';

    return function (options) {

        return Cell.extend({
            /*jshint validthis:true */

            View: View,

            init: function () {
                this.iconWidgets = [];
            },

            onCellReady: function () {
                if (this.iconWidgets.length > 0) {
                    destroyContent.call(this);
                }

                var element = this.getElement();
                options.icons.forEach(function (iconData) {
                    var iconWidget = new ActionIcon({
                        iconKey: iconData.icon,
                        interactive: (iconData.action !== undefined),
                        title: iconData.title
                    });
                    if (iconData.action !== undefined) {
                        iconWidget.addEventHandler('click', function (e) {
                            e.preventDefault();
                            iconData.action.call(null, this.options.row.getData(), e);
                        }, this);
                    }
                    iconWidget.attachTo(element);
                    this.iconWidgets.push(iconWidget);
                }.bind(this));
            }

        });

        function destroyContent () {
            this.iconWidgets.forEach(function (iconWidget) {
                iconWidget.destroy();
            });
            this.iconWidgets = [];
        }

    };

});

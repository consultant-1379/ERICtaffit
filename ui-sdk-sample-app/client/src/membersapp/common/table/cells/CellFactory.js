define([
    './FormatCell/FormatCellFactory',
    './IconsCell/IconsCellFactory',
    './LinkCell/LinkCellFactory',
    './ObjectCell/ObjectCellFactory',
    './ObjectsCell/ObjectsCellFactory',
    './ColoredCell/ColoredCellFactory',
    './ButtonCell/ButtonCellFactory'
], function (formatCell, iconsCell, linkCell, objectCell, objectsCell, coloredCell, buttonCell) {
    'use strict';

    return {
        format: function (options) {
            return formatCell(options);
        },

        icons: function (options) {
            return iconsCell(options);
        },

        link: function (options) {
            return linkCell(options);
        },

        object: function (options) {
            return objectCell(options);
        },

        objects: function (options) {
            return objectsCell(options);
        },

        colored: function (options) {
            return coloredCell(options);
        },

        button: function (options) {
            return buttonCell(options);
        }
    };
});

/*global define*/
define([], function () {
    'use strict';

    return {

        /**
         * Removes spaces in the string from beginning and from end.
         *
         * @param {string} string
         * @returns {string}
         */
        trim: function (string) {
            if (string === null) {
                return null;
            }
            return string.replace(/^\s+|\s+$/g, '');
        }

    };

});

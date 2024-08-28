define([
    'jscore/core'
], function (core) {
    'use strict';

    return core.Region.extend({

        show: function () {
            this.view.getElement().setModifier('show', 'true', 'eaMembersApp-Content-page');
            this.onShow.apply(this, arguments);
        },

        hide: function () {
            this.view.getElement().removeModifier('show', 'eaMembersApp-Content-page');
            this.onHide.apply(this, arguments);
        },

        redraw: function () {
            this.onRedraw.apply(this, arguments);
        },

        onShow: function () {
        },
        onHide: function () {
        },
        onRedraw: function () {
        },
        getBreadcrumbs: function () {
            return [];
        }

    });

});

/*global define*/
define([
    'jscore/core',
    './ActionIconView'
], function (core, View) {
    'use strict';

    return core.Widget.extend({

        View: View,

        init: function () {
            this._iconKey = '';
            this._iconSize = '';
        },

        onViewReady: function () {
            var options = this.options;

            if (options.iconKey) {
                this.setIcon(options.iconKey, options.iconValue);
            }
            if (options.iconSize) {
                this.setSize(options.iconSize);
            }
            if (options.interactive) {
                this.setInteractive(true);
            }
            if (options.hide) {
                this.setHidden(options.hide);
            }
            if (options.title) {
                this.setTitle(options.title);
            }

            this.getElement().addEventHandler('click', this.onClick, this);
        },

        onClick: function (e) {
            e.preventDefault();
            this.trigger('click', e);
        },

        setHidden: function (isHidden) {
            if (isHidden) {
                this.getElement().setModifier('hide', undefined, 'eaMembersApp-ActionIcon');
            } else {
                this.getElement().removeModifier('hide', 'eaMembersApp-ActionIcon');
            }
        },

        setIcon: function (iconKey, iconValue) {
            this.removeIcon();

            if (iconKey && iconKey !== '') {
                this.getElement().setModifier(iconKey, iconValue);
            }
            this._iconKey = iconKey;
        },

        removeIcon: function () {
            if (this._iconKey !== '') {
                this.getElement().removeModifier(this._iconKey);
            }
        },

        setSize: function (iconSize) {
            if (this._iconSize) {
                this.getElement().removeModifier(this._iconSize);
            }
            if (iconSize && iconSize !== '' && iconSize !== 'default') {
                this.getElement().setModifier(iconSize);
            }
            this._iconSize = iconSize;
        },

        setInteractive: function (isInteractive) {
            if (isInteractive) {
                this.getElement().setModifier('interactive');
            } else {
                this.getElement().removeModifier('interactive');
            }

        },

        setTitle: function (title) {
            this.getElement().setAttribute('title', title);
        }

    });

});

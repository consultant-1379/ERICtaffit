/*global define*/
define([
    'jscore/core',
    './ActionLinkView',
    '../actionIcon/ActionIcon'
], function (core, View, ActionIcon) {
    'use strict';

    return core.Widget.extend({

        View: View,

        onViewReady: function () {
            if (this.options.icon) {
                this.actionIcon = new ActionIcon(this.options.icon);
                this.actionIcon.attachTo(this.view.getIconHolder());
            }

            this.view.getLink().setText(this.options.link.text);
            this.setTitle(this.options.link.text);

            if (this.options.url) {
                this.setUrl(this.options.url);
            } else if (this.options.action) {
                this.addEventAction('click', function (e) {
                    e.preventDefault();
                    this.options.action.apply(null, arguments);
                }.bind(this));
            }

            if (this.options.id) {
                this.getElement().setAttribute('id', this.options.id);
            }
        },

        setHidden: function (isHidden) {
            if (isHidden) {
                this.getElement().setModifier('hide', undefined, 'eaMembersApp-ActionLink');
            } else {
                this.getElement().removeModifier('hide', 'eaMembersApp-ActionLink');
            }
        },

        getActionIcon: function () {
            return this.actionIcon;
        },

        setLinkText: function (text) {
            this.view.getLink().setText(text);
            this.setTitle(text);
            if (this.actionIcon) {
                this.actionIcon.setTitle(text);
            }
        },

        setTitle: function (title) {
            this.getElement().setAttribute('title', title);
        },

        setUrl: function (url) {
            this.view.getLink().setAttribute('href', '#' + url);
        },

        addEventAction: function (eventName, callBack, context) {
            this.view.getLink().addEventHandler(eventName, callBack, context);
            if (this.actionIcon) {
                this.actionIcon.addEventHandler(eventName, callBack, context);
            }
        }
    });

});

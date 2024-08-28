define([
    'jscore/ext/mvp'
], function (mvp) {
    'use strict';

    return mvp.Model.extend({

        url: '/api/members',

        getId: function () {
            return this.getAttribute('id');
        },

        getName: function () {
            return this.getAttribute('name');
        },

        getSurname: function () {
            return this.getAttribute('surname');
        },

        setRole: function (roleObj) {
            this.setAttribute('role', roleObj);
        },

        setTeams: function (teams) {
            this.setAttribute('teams', teams);
        },

        setGender: function (genderObj) {
            this.setAttribute('gender', genderObj);
        },

        setHasLaptop: function (hasLaptop) {
            this.setAttribute('hasLaptop', hasLaptop);
        },

        setHasAccess: function (hasAccess) {
            this.setAttribute('hasAccess', hasAccess);
        },

        clear: function () {
            var model = this._model;
            return model.clear.apply(model, arguments);
        }

    });

});

/*global window, location*/
define([
    'jscore/core',
    'jscore/ext/utils/base/underscore',
    '../lib/Crossroads',
    'widgets/Breadcrumb',
    '../common/Constants'
], function (core, _, Crossroads, Breadcrumb, Constants) {
    /*jshint validthis:true*/
    'use strict';

    function onHashChange () {
        var match = location.href.match(/#(.*)$/);
        Crossroads.parse(match ? match[1] : '');
    }

    function removeChildren (children) {
        children.forEach(function (childEl) {
            childEl.remove();
        });
    }

    var pageLoaded = null;

    function applyContentRegion (pageId, attributesObj, queryObj) {
        if (this.pagesObj[pageId]) {
            var pageToOpen = this.pagesObj[pageId];

            if (pageLoaded !== null) {
                if (pageLoaded.groupId === pageToOpen.groupId) {
                    pageToOpen.regionObj.redraw(attributesObj, queryObj);
                } else {
                    pageLoaded.regionObj.hide();

                    if (pageToOpen.isCreated) {
                        pageToOpen.regionObj.show();
                    }
                }
            }

            if (pageLoaded === null || !pageToOpen.isCreated) {
                pageToOpen.regionObj = new pageToOpen.ContentRegion({context: this.appContext});
                pageToOpen.regionObj.start(this.contentHolderEl);
                pageToOpen.regionObj.show(attributesObj, queryObj);
                pageToOpen.isCreated = true;
            }

            pageLoaded = pageToOpen;

            applyBreadcrumbs.call(this, pageLoaded);
        }
    }

    function applyBreadcrumbs (pageLoaded) {
        removeChildren(this.breadcrumbHolderEl.children());
        if (pageLoaded.breadcrumbObj) {
            pageLoaded.breadcrumbObj.destroy();
        }

        var breadcrumbs = pageLoaded.regionObj.getBreadcrumbs();
        if (_.isEmpty(breadcrumbs)) {
            return;
        }

        pageLoaded.breadcrumbObj = new Breadcrumb({
            data: breadcrumbs
        });
        pageLoaded.breadcrumbObj.attachTo(this.breadcrumbHolderEl);
    }

    function showMembersListPage (queryObj) {
        applyContentRegion.call(this, Constants.pages.MEMBERS_LIST_PAGE, queryObj);
    }

    function showAddMemberPage () {
        applyContentRegion.call(this, Constants.pages.ADD_MEMBER_PAGE);
    }

    var AppRouter = function (pagesObj, appContext, contentHolderEl, breadcrumbHolderEl) {
        this.pagesObj = pagesObj;
        this.appContext = appContext;
        this.contentHolderEl = contentHolderEl;
        this.breadcrumbHolderEl = breadcrumbHolderEl;
    };

    AppRouter.prototype.getLoadedPage = function () {
        return pageLoaded;
    };

    AppRouter.prototype.start = function () {
        Crossroads.addRoute('membersapp', showMembersListPage.bind(this));
        Crossroads.addRoute('membersapp/list:?query:', showMembersListPage.bind(this));
        Crossroads.addRoute('membersapp/add', showAddMemberPage.bind(this));

        window.addEventListener('hashchange', onHashChange, false);
        onHashChange();
    };

    AppRouter.prototype.stop = function () {
        window.removeEventListener('hashchange');
    };

    return AppRouter;

});

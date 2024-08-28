define([
    'jscore/ext/utils/base/underscore',
    'widgets/Pagination',
    'tablelib/plugins/SecondHeader',
    'tablelib/plugins/SortableHeader',
    './plugins/IdentifiableTable/IdentifiableTable',
    './plugins/IdentifiableRows/IdentifiableRows',
    './../ModelHelper'
], function (_, Pagination, SecondHeader, SortableHeader, IdentifiableTable, IdentifiableRows,
            ModelHelper) {
    'use strict';

    var TableHelper = function (options) {
        this.isPaginated = options.isPaginated;
        this.localStorageNamespace = options.localStorageNamespace;
        this.collection = options.collection;
        if (this.isPaginated) {
            this.count = this.collection.getTotalCount();
            this.page = this.collection.getPage();
            this.pagination = null;
            this.parent = options.parent;
        }
        this.eventBus = options.eventBus;
        this.data = options.data || {};
        this.table = options.table;

        this.sortData = {};
        this.filters = {};
    };

    TableHelper.prototype.updatePage = function () {
        var currentCount = this.collection.getTotalCount();
        var currentPage = this.collection.getPage();
        var perPage = this.collection.getPerPage();
        if (currentCount === this.count && currentPage === this.page && perPage === this.perPage) {
            return;
        }
        this.count = currentCount;
        this.page = currentPage;
        this.perPage = perPage;

        if (this.pagination != null) {
            this.pagination.destroy();
            this.pagination = null;
        }

        if (this.collection.getTotalCount() > perPage) {
            var pages = Math.ceil(this.collection.getTotalCount() / perPage);
            this.pagination = new Pagination({
                pages: pages,
                onPageChange: function (pageNumber) {
                    if (this.collection.getPage() !== pageNumber) {
                        this.collection.setPage(pageNumber);
                        this.page = pageNumber;

                        this.collection.fetch({
                            reset: true,
                            data: this.data,
                            statusCode: ModelHelper.statusCodeHandler(this.eventBus)
                        });
                    }
                }.bind(this)
            });
            this.pagination.attachTo(this.parent);
        }
    };

    TableHelper.prototype.applySortAndFilter = function () {
        this.table.addEventHandler('sort', this.sort, this);
        this.table.addEventHandler('filter', this.filter, this);
    };

    TableHelper.prototype.applyCollectionReset = function () {
        this.collection.addEventHandler('reset', function (collection) {
            this.updatePage();
            this.table.setData(collection.toJSON());
        }, this);
    };

    TableHelper.prototype.applyLocalCollectionReset = function () {
        this.collection.addEventHandler('reset', function (collection) {
            this.table.setData(collection.toJSON());
        }, this);
    };

    TableHelper.prototype.applyLocalFilter = function () {
        this.table.addEventHandler('filter', this.localFilter, this);
    };

    TableHelper.prototype.sort = function (sortMode, sortAttr) {
        var sortObj = {sortMode: sortMode, sortAttr: sortAttr};
        if (_.isEqual(this.sortData, sortObj)) {
            return;
        }
        this.sortData = sortObj;

        this.collection.resetPage();
        this.collection.setSortData(this.sortData);
        this.collection.fetch({
            reset: true,
            data: this.data,
            statusCode: ModelHelper.statusCodeHandler(this.eventBus)
        });
    };

    TableHelper.prototype.filter = function (attribute, value, comparator) {
        if (_.isEmpty(value)) {
            delete this.filters[attribute];
        } else {
            this.filters[attribute] = {value: value, comparator: comparator};
        }

        this.collection.resetPage();
        this.collection.setFiltersData(this.filters);
        this.collection.fetch({
            reset: true,
            data: this.data,
            statusCode: ModelHelper.statusCodeHandler(this.eventBus)
        });
    };

    TableHelper.prototype.localFilter = function (attribute, value, comparator) {
        if (_.isEmpty(value)) {
            delete this.filters[attribute];
        } else {
            this.filters[attribute] = {value: value, comparator: comparator};
        }

        if (_.isEmpty(this.filters)) {
            this.table.setData(this.collection.toJSON());
            return;
        }

        var collection = this.collection;
        _.each(this.filters, function (filterObj, attribute) {
            collection = collection.searchMap(getRegexpPattern(filterObj), [attribute]);
        });
        this.table.setData(collection.toJSON());
    };

    var patterns = {
        '=': '^%s$',
        '!=': '^(?!%s$)'
    };

    function getRegexpPattern (filterObj) {
        if (!patterns.hasOwnProperty(filterObj.comparator)) {
            return filterObj.value;
        }

        var pattern = patterns[filterObj.comparator];
        return new RegExp(pattern.replace('%s', filterObj.value));
    }

    TableHelper.plugins = function (options, additionalPlugins) {
        var defaultPlugins = [
            new SecondHeader(),
            new SortableHeader(),
            new IdentifiableTable({
                attribute: 'id',
                identifier: options.tableId || 'TMS_Table_' + new Date().getTime()
            }),
            new IdentifiableRows({
                rowIdentifier: options.tableRowAttribute || 'id'
            })
        ];

        if (_.isEmpty(additionalPlugins)) {
            return defaultPlugins;
        }
        return _.union(defaultPlugins, additionalPlugins);
    };

    TableHelper.applyColumnsWidths = function (lsNamespace, columns) {
        var ls = localStorage[lsNamespace];
        if (ls) {
            ls = JSON.parse(ls);
            columns.forEach(function (col) {
                if (ls[col.attribute]) {
                    col.width = ls[col.attribute];
                }
            });
        }
    };

    TableHelper.updateColumnWidth = function (lsNamespace, column) {
        var ls = JSON.parse(localStorage[lsNamespace] || '{}');
        ls[column.attribute] = column.width;
        localStorage[lsNamespace] = JSON.stringify(ls);
    };

    return TableHelper;

});

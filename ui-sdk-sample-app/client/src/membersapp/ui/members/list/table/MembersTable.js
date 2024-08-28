define([
    'jscore/core',
    './MembersTableView',
    'widgets/SelectBox',
    'widgets/Pagination',
    'tablelib/Table',
    'tablelib/plugins/Selection',
    '../../../../common/table/TableHelper',
    '../../../../common/table/cells/CellFactory',
    '../../../../common/table/filters/StringFilterCell/StringFilterCell'
], function (core, View, SelectBox, Pagination, Table, Selection, TableHelper, CellFactory, StringFilterCell) {
    'use strict';

    return core.Widget.extend({
        /*jshint validthis:true */

        View: View,

        init: function (options) {
            this.region = options.region;
            this.eventBus = this.region.getEventBus();
            this.membersCollection = options.membersCollection;

            this.columns = [
                {
                    title: 'Name',
                    attribute: 'name',
                    secondHeaderCellType: StringFilterCell,
                    width: '200px',
                    sortable: true
                },
                {
                    title: 'Surname',
                    attribute: 'surname',
                    secondHeaderCellType: StringFilterCell,
                    width: '200px',
                    sortable: true
                },
                {
                    title: 'Role',
                    attribute: 'role',
                    cellType: CellFactory.object({
                        field: 'name'
                    }),
                    secondHeaderCellType: StringFilterCell,
                    sortable: true
                },
                {
                    title: 'Email',
                    attribute: 'email',
                    cellType: CellFactory.link({
                        url: function (model) {
                            return 'mailto:' + model.email;
                        }
                    }),
                    secondHeaderCellType: StringFilterCell,
                    sortable: true
                },
                {
                    title: 'Teams',
                    attribute: 'teams',
                    cellType: CellFactory.objects({
                        field: 'name'
                    }),
                    secondHeaderCellType: StringFilterCell,
                    sortable: true
                }
            ];

            this.tableRowsSelect = new SelectBox({
                value: {name: '10', value: 10, title: '10'},
                items: [
                    {name: '5', value: 5, title: '5'},
                    {name: '10', value: 10, title: '10'},
                    {name: '20', value: 20, title: '20'}
                ],
                modifiers: [
                    {name: 'width', value: 'mini'}
                ]
            });

            createMembersTable.call(this);
        },

        onViewReady: function () {
            this.view.afterRender();

            this.tableRowsSelect.attachTo(this.view.getHeadingCommands());
            this.tableRowsSelect.addEventHandler('change', this.onTableRowsSelectChange, this);

            setupMembersTable.call(this);
        },

        onTableRowsSelectChange: function () {
            var selectedValue = this.tableRowsSelect.getValue();
            this.membersCollection.setPerPage(selectedValue.value);
            this.membersCollection.fetch({reset: true});
        },

        deselectAll: function () {
            this.membersTable.unselectAllRows();
        },

        refreshTable: function () {
            var membersArray = this.membersCollection.toJSON();
            this.membersTable.setData(membersArray);
            this.view.getItemsCount().setText(membersArray.length);
        }

    });

    function createMembersTable () {
        this.membersTable = new Table({
            tooltips: true,
            columns: this.columns,
            modifiers: [
                {name: 'striped'}
            ],
            plugins: TableHelper.plugins({
                tableId: 'MembersApp_Members_table'
            },
            [
                new Selection({
                    checkboxes: true,
                    selectableRows: true,
                    multiselect: true,
                    bind: true
                })
            ])
        });

        this.membersTable.addEventHandler('checkend', function (checkedRows) {
            this.region.updateSelection(checkedRows);
        }, this);
    }

    function setupMembersTable () {
        this.membersTable.attachTo(this.view.getTableHolder());

        var tableHelper = new TableHelper({
            eventBus: this.eventBus,
            collection: this.membersCollection,
            parent: this.view.getTableHolder(),
            table: this.membersTable,
            isPaginated: true
        });
        tableHelper.applySortAndFilter();

        this.membersCollection.addEventHandler('reset', function () {
            this.membersTable.setData(this.membersCollection.toJSON());
            this.view.getItemsCount().setText(this.membersCollection.getTotalCount());
            tableHelper.updatePage();
        }, this);
    }

});

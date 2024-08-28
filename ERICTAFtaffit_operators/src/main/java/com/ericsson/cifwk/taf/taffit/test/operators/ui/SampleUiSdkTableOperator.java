package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.SortDirection;

public interface SampleUiSdkTableOperator {

    boolean tableExists();

    void loadSamplePlainTablePage();

    void loadSampleRightClickTablePage();

    int getTableRowCount();

    int getCellCountInRow(int idx);

    String getCellData(int rowIdx, int columnIdx);

    String getHeadingCellData(int cellIdx);

    boolean isColumnSortable(int columnIdx);

    SortDirection getColumnSortingDirection(int columnIdx);

    void sortColumn(int columnIdx);

    void rightClickRow(int rowIdx);

    boolean contextMenuExists();
}

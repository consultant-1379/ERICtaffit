package com.ericsson.cifwk.taf.taffit.test.operators.ui;

import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.BodyCell;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.BodyRow;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.HeadCell;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.HeadRow;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.SortDirection;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.Table;
import com.ericsson.cifwk.taf.taffit.test.pages.UiSdkSampleTableView;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.UI;

import java.util.List;

@Operator
public class SampleUiSdkTableOperatorImpl implements SampleUiSdkTableOperator {

    private static final TafConfiguration config = TafConfigurationProvider.provide();

    private Browser browser;
    private BrowserTab tab;
    private UiSdkSampleTableView view;
    private Table table;

    @Override
    public boolean tableExists() {
        return table.exists();
    }

    @Override
    public void loadSamplePlainTablePage() {
        loadSampleTablePage(config.getString("sampleTable1Url"));
    }

    @Override
    public void loadSampleRightClickTablePage() {
        loadSampleTablePage(config.getString("sampleRightClickTableUrl"));
    }

    private void loadSampleTablePage(String pageUrl) {
        browser = UI.newBrowser(BrowserType.FIREFOX);
        tab = browser.open(pageUrl);
        view = tab.getView(UiSdkSampleTableView.class);

        view.waitUntilComponentIsDisplayed(".elTablelib-Table", 5000);

        table = view.getTable();
    }

    @Override
    public int getTableRowCount() {
        List<BodyRow> tableRows = table.getBodyRows();
        return tableRows.size();
    }

    @Override
    public int getCellCountInRow(int idx) {
        BodyRow firstRow = table.getBodyRow(idx);
        return firstRow.getCells().size();
    }

    @Override
    public String getCellData(int rowIdx, int columnIdx) {
        BodyRow row = table.getBodyRow(rowIdx);
        BodyCell cell = row.getCell(columnIdx);
        return cell.getText();
    }

    @Override
    public String getHeadingCellData(int cellIdx) {
        HeadCell cell = getHeadCell(cellIdx);
        return cell.getText();
    }

    @Override
    public boolean isColumnSortable(int columnIdx) {
        HeadCell firstCell = getHeadCell(columnIdx);
        return firstCell.isSortable();
    }

    @Override
    public SortDirection getColumnSortingDirection(int columnIdx) {
        HeadCell headCell = getHeadCell(columnIdx);
        return headCell.getDirection();
    }

    @Override
    public void sortColumn(int columnIdx) {
        HeadCell headCell = getHeadCell(columnIdx);
        headCell.sort();
    }

    @Override
    public void rightClickRow(int rowIdx) {
        BodyRow row = table.getBodyRow(rowIdx);
        row.contextClick();
    }

    @Override
    public boolean contextMenuExists() {
        return view.getContextMenu().exists();
    }

    private HeadCell getHeadCell(int cellIdx) {
        HeadRow headRow = table.getHeadRow();
        return headRow.getCell(cellIdx);
    }

//    Table table = view.getTable();
//
//    HeadCell firstCell = table.getHeadRow().getCell(0);
//    HeadCell secondCell = table.getHeadRow().getCell(1);
//    HeadCell thirdCell = table.getHeadRow().getCell(2);
//
//    assertTrue(firstCell.isSortable());
//    assertTrue(secondCell.isSortable());
//    assertFalse(thirdCell.isSortable());
//
//    BodyRow firstRow = table.getBodyRow(0);
//    BodyCell firstBodyCell = firstRow.getCell(0);
//
//    assertEquals("Phoenix", firstBodyCell.getText());
//    assertNull(firstCell.getDirection());
//
//    firstCell.sort();
//    assertEquals("Ema", firstBodyCell.getText());
//    assertEquals(SortDirection.ASC, firstCell.getDirection());
//
//    firstCell.sort();
//    assertEquals("Phoenix", firstBodyCell.getText());
//    assertEquals(SortDirection.DESC, firstCell.getDirection());

}

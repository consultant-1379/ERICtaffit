package com.ericsson.cifwk.taf.taffit.test.cases.ui;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.SampleUiSdkTableOperator;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.SampleUiSdkTableOperatorImpl;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.SortDirection;
import com.google.inject.Provider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class UiCustomComponentsTest extends TafTestBase {

    @Inject
    private Provider<SampleUiSdkTableOperatorImpl> provider;

    private SampleUiSdkTableOperator operator;

    @BeforeMethod
    public void setUp() {
        operator = provider.get();
        operator.loadSamplePlainTablePage();
        assertTrue(operator.tableExists());
    }

    @Test
    @TestId(id = "TAF_UI_Func_11")
    public void shouldMapToTableContent() {
        assertEquals(5, operator.getTableRowCount());

        assertEquals(3, operator.getCellCountInRow(0));

        assertEquals("Phoenix", operator.getCellData(0, 0));
        assertEquals("Wright", operator.getCellData(0, 1));
        assertEquals("Defense Attorney", operator.getCellData(0, 2));

        assertEquals("First Name", operator.getHeadingCellData(0));
        assertEquals("Last Name", operator.getHeadingCellData(1));
        assertEquals("Occupation", operator.getHeadingCellData(2));
    }

    @Test
    @TestId(id = "TAF_UI_Func_12")
    public void shouldSortTable() {
        assertTrue(operator.isColumnSortable(0));
        assertTrue(operator.isColumnSortable(1));
        assertFalse(operator.isColumnSortable(2));

        assertEquals("Phoenix", operator.getCellData(0, 0));
        assertNull(operator.getColumnSortingDirection(0));

        operator.sortColumn(0);

        assertEquals("Ema", operator.getCellData(0, 0));
        assertEquals(SortDirection.ASC, operator.getColumnSortingDirection(0));

        operator.sortColumn(0);

        assertEquals("Phoenix", operator.getCellData(0, 0));
        assertEquals(SortDirection.DESC, operator.getColumnSortingDirection(0));
    }
}

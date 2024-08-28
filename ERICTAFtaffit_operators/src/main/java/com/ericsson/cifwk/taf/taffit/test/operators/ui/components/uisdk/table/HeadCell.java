package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.google.common.base.Preconditions;

import static com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.SortDirection.ASC;
import static com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.SortDirection.DESC;

/**
 * UI SDK table head cell
 */
public class HeadCell extends AbstractUiComponent implements Cell {

    @UiComponentMapping(".ebTable-headerSort.ebSort")
    private UiComponent sortableIcon;

    @UiComponentMapping(".ebSort-arrow_up")
    private SortArrow upArrow;

    @UiComponentMapping(".ebSort-arrow_down")
    private SortArrow downArrow;

    public void sort() {
        Preconditions.checkState(isSortable(), "This column is not sortable");
        sortableIcon.click();
    }

    public boolean isSorted() {
        return isSortable() && (upArrow.isActive() || downArrow.isActive());
    }

    public SortDirection getDirection() {
        Preconditions.checkState(isSortable(), "This column is not sortable");

        if (!isSorted()) {
            return null;
        }

        return upArrow.isActive() ? ASC : DESC;
    }

    public boolean isSortable() {
        return sortableIcon.exists();
    }

}

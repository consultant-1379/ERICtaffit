package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Sample control for dealing with UI SDK table - https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/tablelib/latest/examples/#example1
 */
public class Table extends AbstractUiComponent {

    @UiComponentMapping("table thead .ebTableRow")
    private HeadRow headRow;

    @UiComponentMapping("table tbody .ebTableRow")
    private List<BodyRow> bodyRows;

    public HeadRow getHeadRow() {
        return headRow;
    }

    public List<BodyRow> getBodyRows() {
        return bodyRows;
    }

    public BodyRow getBodyRow(int rowIndex) {
        Preconditions.checkArgument(rowIndex > -1, "rowIndex should be non negative");
        return (rowIndex < bodyRows.size()) ? bodyRows.get(rowIndex) : null;
    }
}

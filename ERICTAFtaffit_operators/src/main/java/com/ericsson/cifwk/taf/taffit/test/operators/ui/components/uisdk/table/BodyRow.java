package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;

import java.util.List;

/**
 * UI SDK table body row
 */
public class BodyRow extends AbstractHeadRow<BodyCell> {

    @UiComponentMapping("td")
    private List<BodyCell> columns;

    public List<BodyCell> getCells() {
        return columns;
    }

}

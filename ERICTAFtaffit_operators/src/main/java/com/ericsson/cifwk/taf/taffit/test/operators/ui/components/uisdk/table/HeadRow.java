package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;

import java.util.List;

public class HeadRow extends AbstractHeadRow<HeadCell> {

    @UiComponentMapping("th")
    private List<HeadCell> cells;

    public List<HeadCell> getCells() {
        return cells;
    }

}

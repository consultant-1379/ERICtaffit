package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.google.common.base.Preconditions;

import java.util.List;

public abstract class AbstractHeadRow<T extends Cell> extends AbstractUiComponent implements Row<T> {

    public T getCell(int cellIndex) {
        Preconditions.checkArgument(cellIndex >= 0, "cellIndex should be non negative");
        List<T> cells = getCells();
        return (cellIndex < cells.size()) ? cells.get(cellIndex) : null;
    }

}

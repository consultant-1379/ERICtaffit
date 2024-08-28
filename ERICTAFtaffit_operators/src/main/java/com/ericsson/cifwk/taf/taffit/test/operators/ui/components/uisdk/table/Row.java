package com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table;

import java.util.List;

/**
 * UI SDK table row
 */
public interface Row<T extends Cell> {

    List<T> getCells();

    T getCell(int cellIndex);
}

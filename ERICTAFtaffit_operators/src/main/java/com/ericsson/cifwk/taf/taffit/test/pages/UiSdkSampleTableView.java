package com.ericsson.cifwk.taf.taffit.test.pages;

import com.ericsson.cifwk.taf.taffit.test.operators.ui.components.uisdk.table.Table;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

public class UiSdkSampleTableView extends GenericViewModel {

    @UiComponentMapping(".elTablelib-Table")
    private Table table;

    @UiComponentMapping(".ebComponentList")
    private UiComponent contextMenu;

    public Table getTable() {
        return table;
    }

    public UiComponent getContextMenu() {
        return contextMenu;
    }
}

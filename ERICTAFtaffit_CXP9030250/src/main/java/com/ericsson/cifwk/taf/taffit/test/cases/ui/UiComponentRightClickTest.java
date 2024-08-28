package com.ericsson.cifwk.taf.taffit.test.cases.ui;


import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.taffit.test.operators.ui.SampleUiSdkTableOperator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Provider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UiComponentRightClickTest extends TafTestBase {

    @Inject
    private Provider<SampleUiSdkTableOperator> provider;
    private SampleUiSdkTableOperator operator;

    @BeforeMethod
    public void setUp() {
        operator = provider.get();
        operator.loadSampleRightClickTablePage();
        assertTrue(operator.tableExists());
    }

    @Test
    @TestId(id = "TAF_UI_Func_24")
    public void shouldOpenContextMenuWithRightClick() {
        assertFalse(operator.contextMenuExists());

        operator.rightClickRow(0);

        assertTrue(operator.contextMenuExists());
    }

}

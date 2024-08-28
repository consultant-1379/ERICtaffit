/*
 * COPYRIGHT Ericsson (c) 2014.
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.cifwk.taf.taffit.test.cases.scenario;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.DataSource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;

public class DataDrivenTest {

    private AtomicInteger counter1 = new AtomicInteger(0);
    private AtomicInteger counter2 = new AtomicInteger(0);
    private AtomicBoolean counter1Initialized = new AtomicBoolean(false);
    private AtomicBoolean counter2Initialized = new AtomicBoolean(false);

    @AfterClass
    public void tearDown() {
        if (counter1Initialized.get()) {
            assertEquals(counter1.get(), 2);
        }
        if (counter2Initialized.get()) {
            assertEquals(counter2.get(), 3);
        }
    }

    @Test
    @TestId(id = "TAF_Scenarios_0010")
    @DataDriven(name = "scenarios-data-source1")
    public void csvDataSource(@Input("a") int x, @Input("b") int y, @Input("c") int z) {
        assertInputs(x, y, z);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0011")
    @DataDriven(name = "scenarios-data-source2")
    public void csvDataSourceWithCustomDelimeter(@Input("d") int x, @Input("e") int y, @Input("f") int z) {
        assertInputs(x, y, z);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0012")
    @DataDriven(name = "scenarios-data-source1")
    public void complexCsv(@Input("arrayOfStrings") String[] strings, @Input("length") int length) {
        assertEquals(strings.length, length);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0012")
    @DataDriven(name = "scenarios-data-source2")
    public void complexCsvCustomDelimeter(@Input("arrayOfStrings") String[] strings, @Input("length") int length) {
        assertEquals(strings.length, length);
    }

    @Test
    @TestId(id = "TAF_Scenarios_0013")
    @DataDriven(name = "scenarios-data-source1")
    public void complexCsvDataSourceStringWithSeparator(@Input("arrayOfStrings") String string) {
        assertEquals("test1,test2", string.replace(";", ","));
    }

    @Test
    @TestId(id = "TAF_Scenarios_0012")
    @DataDriven(name = "scenarios-data-source-java1")
    public void javaDataSource(@Input("String") String a, @Input("array") String[] aa, @Input("mixedArray") String[] aaa) {
        assertEquals("te,st", a);
        assertEquals("[te, st]", Arrays.toString(aa));
        assertEquals("[te,st, s]", Arrays.toString(aaa));
    }

    private void assertInputs(int x, int y, int z) {
        if (x == 1) {
            assertEquals(2, y);
            assertEquals(3, z);
        } else if (x == 4) {
            assertEquals(5, y);
            assertEquals(6, z);
        } else {
            throw new IllegalArgumentException(String.format("Unexpected input '%s' from data source", x));
        }
    }

    public static class JavaDataSource {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            Map<String, Object> record = new HashMap<String, Object>();
            record.put("String", "te,st");
            record.put("array", "te,st");
            record.put("mixedArray", "\"te,st\",s");
            record.put("int", 1);
            record.put("long", 2L);
            record.put("BigDecimal", new BigDecimal("3.00"));
            records.add(record);
            return records;
        }
    }
}
package com.ericsson.cifwk.taf.taffit.test.cases;

import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TransactionWithData {

	@TestId(id = "CIP-3305_Func_1", title = "Compares three input parameters with three cell values of a csv file")
    @Test
    @DataDriven(name = "transaction")
    public void a(@Input("x1") int x, @Input("x2") int y, @Input("x3") int z) {
        assertEquals(1, x);
        assertEquals(2, y);
        assertEquals(3, z);
    }

	@TestId(id = "CIP-3305_Func_2", title = "Compares two input parameters with two cell values of a csv file")
    @Test
    @DataDriven(name = "transaction")
    public void b(@Input("x2") int y, @Input("x3") int z){
        assertEquals(2, y);
        assertEquals(3, z);
    }

	@TestId(id = "CIP-3305_Func_3", title = "Compares first input parameter with a cell value of a csv file")
    @Test
    @DataDriven(name = "transaction")
    public void c(@Input("x3") int z) {
        assertEquals(3, z);
    }
}

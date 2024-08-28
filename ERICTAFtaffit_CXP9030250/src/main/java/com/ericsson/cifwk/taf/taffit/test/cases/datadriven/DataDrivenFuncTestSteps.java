package com.ericsson.cifwk.taf.taffit.test.cases.datadriven;

import java.util.concurrent.atomic.AtomicInteger;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DataDrivenFuncTestSteps extends TafTestBase {

    public static final String TRANSFORMED_SHARED_INCREMENT_TS = "transformedSharedIncrementTestStep";
    public static final String TRANSFORMED_SHARED_VERIFY_TS = "transformedSharedVerifyTestStep";
    public static final String TRANSFORMED_SHARED_RESET_TS = "transformedSharedResetTestStep";

    private AtomicInteger strategyTransformed = new AtomicInteger(0);
    private AtomicInteger transformedShared = new AtomicInteger(0);

    @TestStep(id = "verifyTransformMergeCombine")
    public void verifyMultipleDataSourcesWithTafDataSourcesFunctionality(@Input("instrument") String instrument) {
        assertEquals("piano", instrument);
    }

    @TestStep(id = "verifyCombineMergeTransform")
    public void verifyMultipleDataSourcesWithTafDataSourcesFunctionalityReversed(@Input("name") String name) {
        assertEquals("Joe Bloggs", name);
    }
    
    @TestStep(id = "verifyMultipleCombineMergeTransform")
    public void verifyMultipleCombineMergeTransform(@Input("colour") String colour, @Input("instrument") String instrument, @Input("fruit") String fruit, @Input("animal") String animal, @Input("name") String name) {
        assertEquals("Joe Bloggs", name);
        assertNotNull(animal);
    }

    @TestStep(id = "verifyCorrectExecution")
    public void verifyStrategyIsTransformedFromOneDataSourceToAnother() {
        strategyTransformed.incrementAndGet();
    }

    @TestStep(id = "verifyCorrectExecutionResult")
    public void verifyStrategyIsTransformedResult() {
        assertEquals(strategyTransformed.get(), 20);
    }

    @TestStep(id = DataDrivenFuncTestSteps.TRANSFORMED_SHARED_INCREMENT_TS)
    public void incrementTransformedSharedDataSource(@Input("name") String name, @Input("instrument") String instrument){
        assertEquals("Joe Bloggs", name);
        transformedShared.incrementAndGet();
    }

    @TestStep(id =DataDrivenFuncTestSteps.TRANSFORMED_SHARED_VERIFY_TS)
    public void verifyTransformedShareDataSource(){
        assertEquals(transformedShared.get(), 10);
    }

    @TestStep(id = DataDrivenFuncTestSteps.TRANSFORMED_SHARED_RESET_TS)
    public void resetTransformedSharedDataSource(){
        transformedShared.set(0);
    }

}

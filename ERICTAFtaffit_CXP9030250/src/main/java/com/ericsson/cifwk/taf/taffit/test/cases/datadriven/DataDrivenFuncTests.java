package com.ericsson.cifwk.taf.taffit.test.cases.datadriven;

import static org.testng.Assert.assertEquals;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.DataProviders;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.datasource.DataSourceControl;
import com.ericsson.cifwk.taf.taffit.test.cases.datadriven.recordInterfaces.MonthsAndDaysDataRecord;
import com.ericsson.cifwk.taf.taffit.test.cases.datadriven.recordInterfaces.NameAndEmailDataRecord;

public class DataDrivenFuncTests extends TafTestBase {

    private AtomicInteger numberOfExecutionsWithFilterAndStrategy = new AtomicInteger(0);
    private AtomicInteger numberOfExecutionsWithDifferentUsage = new AtomicInteger(0);
    private AtomicInteger multipleDataSourceWithDifferentStrategies = new AtomicInteger(0);
    private AtomicInteger numberOfExecutionsWithMissingDataInputInt = new AtomicInteger(0);
    private AtomicInteger numberOfExecutionsWithMissingDataInputString = new AtomicInteger(0);

    @AfterClass
    public void tearDown() {
        assertEquals(numberOfExecutionsWithFilterAndStrategy.get(), 10);
        assertEquals(numberOfExecutionsWithDifferentUsage.get(), 10);
        assertEquals(multipleDataSourceWithDifferentStrategies.get(), 9);
        assertEquals(numberOfExecutionsWithMissingDataInputInt.get(), 3);
        assertEquals(numberOfExecutionsWithMissingDataInputString.get(), 3);
    }

    @Test
    @TestId(id = "TAF_DDT_Func_001")
    @DataProviders({ @DataDriven(name = "nameAndSignum", filter = "name=='Carol'"), @DataDriven(name = "ageAndSport", filter = "age==24"),
            @DataDriven(name = "genderAndRole", filter = "gender=='male'"), @DataDriven(name = "countyAndTown"), @DataDriven(name = "countryAndCapital"),
            @DataDriven(name = "makeAndModel") })
    public void verifyCorrectNumberOfExecutionsWithFiltersAndStrategy(@Input("name") String name, @Input("age") int age, @Input("gender") String gender) {
        numberOfExecutionsWithFilterAndStrategy.incrementAndGet();
    }

    @Test
    @TestId(id = "TAF_DDT_Func_002")
    @DataProviders({ @DataDriven(name = "firstnameAndLastname"), @DataDriven(name = "monthAndStarSign") })
    public void verifyCorrectNumberOfExecutionsWithDifferentUsage() {
        numberOfExecutionsWithDifferentUsage.incrementAndGet();
    }

    @Test
    @TestId(id = "TAF_DDT_Func_003")
    @DataProviders({ @DataDriven(name = "fruit"), @DataDriven(name = "sport") })
    public void verifyMultipleDataSourcesWithDifferentStrategiesAndStopExecution(@Input("sportType") String sportType) {

        if (sportType.equals("squash")) {
            DataSourceControl.stopExecution("sport");
        }
        multipleDataSourceWithDifferentStrategies.incrementAndGet();
    }

    @Test
    @TestId(id = "TAF_DDT_Func_006")
    @DataProviders({ @DataDriven(name = "monthAndDays"), @DataDriven(name = "nameAndEmail") })
    public void verifyMultipleDataSourcesMapToDataRecordObjects(
            @Input("monthAndDays") MonthsAndDaysDataRecord monthAndDays,
            @Input("nameAndEmail") NameAndEmailDataRecord nameAndEmail) {

        assertEquals("September", monthAndDays.getMonth());
        assertEquals(30, monthAndDays.getDays());

        assertEquals("Joe Bloggs", nameAndEmail.getName());
        assertEquals("jbloggs@hotmail.com", nameAndEmail.getEmail());
    }

    @Test
    @TestId(id = "TAF_DDT_Func_010")
    @DataProviders({ @DataDriven(name = "monthAndDays"), @DataDriven(name = "nameAndEmail") })
    public void verifyMultipleDataSourcesWithDataRecordAndInputOutput(
            @Input("monthAndDays") MonthsAndDaysDataRecord monthAndDays,
            @Input("name") String name,
            @Output("email") String email) {

        assertEquals("September", monthAndDays.getMonth());
        assertEquals("Joe Bloggs", name);
        assertEquals("jbloggs@hotmail.com", email);
    }


    @Test
    @TestId(id = "TAF_DDT_Func_011")
    @DataDriven(name="missingDataInt")
    public void verifyTestSuiteDoesntEndExecutionWithMissingInput(@Input("numberWord") String word, @Input("numberInt") int number){
        assertThat(number).isEqualTo(0);
        numberOfExecutionsWithMissingDataInputInt.incrementAndGet();
    }

    @Test
    @TestId(id = "TAF_DDT_Func_011")
    @DataDriven(name="missingDataString")
    public void verifyTestSuiteDoesntEndExecutionWithMissingString(@Input("numberWord") String word, @Input("numberInt") int number){
        numberOfExecutionsWithMissingDataInputString.getAndIncrement();
    }
}

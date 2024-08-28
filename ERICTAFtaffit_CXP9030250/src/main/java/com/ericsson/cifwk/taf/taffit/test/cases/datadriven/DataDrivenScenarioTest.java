package com.ericsson.cifwk.taf.taffit.test.cases.datadriven;

import java.util.Map;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.TafTestBase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.DataRecordImpl;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.taffit.test.cases.datadriven.DataSourceContainer.Animals;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;

public class DataDrivenScenarioTest extends TafTestBase {

    @Inject
    DataDrivenFuncTestSteps dataDrivenFuncTestSteps;

    private TestDataSource<DataRecord> colorsDataSource;
    private TestDataSource<DataRecord> animalsDataSource;
    private TestDataSource<DataRecord> instrumentsDataSource;
    private TestDataSource<DataRecord> fruitDataSource;
    private TestDataSource<DataRecord> occupationDataSource;

    @BeforeClass
    public void setUp() {
        colorsDataSource = TafDataSources.fromCsv("data/colour.csv");
        animalsDataSource = TafDataSources.fromClass(Animals.class);
        instrumentsDataSource = TafDataSources.fromCsv("data/instruments.csv");
        fruitDataSource = TafDataSources.fromCsv("data/fruit.csv");
        occupationDataSource = TafDataSources.fromCsv("data/occupation.csv");
    }

    @SuppressWarnings("unchecked")
    @Test(enabled = false) //TODO - waiting on fix from CIS-7000
    @TestId(id = "TAF_DDT_Func_007")
    public void transformMergeThenCombine() throws InterruptedException {

        TestDataSource<DataRecord> colorsTransformed = TafDataSources.transform(colorsDataSource, new Transform());
        TestDataSource<DataRecord> colorsAndAnimalsMerged = TafDataSources.merge(colorsTransformed, animalsDataSource);
        TestDataSource<DataRecord>[] dataSources = new TestDataSource[]{colorsAndAnimalsMerged, instrumentsDataSource};
        TestDataSource<DataRecord> colorsAndAnimalsMergedAndCombinedWithInstruments = TafDataSources.combine(dataSources);
        TafTestContext.getContext().addDataSource("dataSourceComplete", colorsAndAnimalsMergedAndCombinedWithInstruments);

        TestStepFlow transformMergeCombine = flow("TransformMergeCombine").addTestStep(annotatedMethod(dataDrivenFuncTestSteps, "verifyTransformMergeCombine"))
                .withDataSources(dataSource("dataSourceComplete")).build();

        TestScenario scenario = scenario("Result").addFlow(transformMergeCombine).build();
        runner().build().start(scenario);

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @TestId(id = "TAF_DDT_Func_005")
    public void combineMergeThenTransform() throws InterruptedException {
        TestDataSource<DataRecord>[] dataSources = new TestDataSource[]{colorsDataSource, animalsDataSource};
        TestDataSource<DataRecord> colorsAndAnimalsCombined = TafDataSources.combine(dataSources);
        TestDataSource<DataRecord> colorsAndAnimalsCombinedAndMergedWithInstruments = TafDataSources.merge(colorsAndAnimalsCombined, instrumentsDataSource);

        TestDataSource<DataRecord> colorsAndAnimalsCombinedAndMergedWithInstrumentsTransformed = TafDataSources.transform(
                colorsAndAnimalsCombinedAndMergedWithInstruments, new Transform());

        TafTestContext.getContext().addDataSource("dataSourceComplete", colorsAndAnimalsCombinedAndMergedWithInstrumentsTransformed);

        TestStepFlow combineMergeTransform = flow("CombineMergeTransform").addTestStep(annotatedMethod(dataDrivenFuncTestSteps, "verifyCombineMergeTransform"))
                .withDataSources(dataSource("dataSourceComplete")).build();

        TestScenario scenario = scenario("Result").addFlow(combineMergeTransform).build();
        runner().build().start(scenario);

    }

    @SuppressWarnings("unchecked")
    @Test
    @TestId(id = "TAF_DDT_Func_012")
    public void multipleCombineMergeThenTransform() throws InterruptedException {

        TestDataSource<DataRecord> colorsInstrumentsAndFruitCombined = TafDataSources.combine(colorsDataSource, instrumentsDataSource, fruitDataSource);
        TestDataSource<DataRecord> colorsInstrumentsAndFruitCombinedAndMergedWithAnimals = TafDataSources.merge(colorsInstrumentsAndFruitCombined,animalsDataSource);

        TestDataSource<DataRecord> colorsInstrumentsAndFruitCombinedAndMergedWithAnimalsTransformed = TafDataSources.transform(
                colorsInstrumentsAndFruitCombinedAndMergedWithAnimals, new Transform());
        TafTestContext.getContext().addDataSource("dataSourceComplete", colorsInstrumentsAndFruitCombinedAndMergedWithAnimalsTransformed);
        TestStepFlow combineMergeTransform = flow("CombineMergeTransform").addTestStep(annotatedMethod(dataDrivenFuncTestSteps, "verifyMultipleCombineMergeTransform"))
                .withDataSources(dataSource("dataSourceComplete")).build();

        TestScenario scenario = scenario("Result").addFlow(combineMergeTransform).build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id="TAF_DDT_Func_011")
    public void transformDataSourceFollowedByShare(){
        TestDataSource<DataRecord> sharedTransformedInstrumentDataSource = TafDataSources.shared(TafDataSources.transform(TafDataSources.fromCsv("data/instruments.csv"), new Transform()));

        TafTestContext.getContext().addDataSource("sharedTransformedInstrumentDataSource", sharedTransformedInstrumentDataSource);

        TestStepFlow flow = flow("Transformed Shared Data Source Flow")
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_INCREMENT_TS))
                .withDataSources(dataSource("sharedTransformedInstrumentDataSource"))
                .build();

        TestStepFlow verifyFlow = flow("Verify Transformed Shared Data Source Flow")
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_VERIFY_TS))
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_RESET_TS))
                .build();

        TestScenario scenario = scenario().addFlow(flow).withDefaultVusers(5).build();
        runner().build().start(scenario);
        scenario = scenario().addFlow(verifyFlow).build();
        runner().build().start(scenario);
        TafTestContext.getContext().removeDataSource("sharedTransformedInstrumentDataSource");
    }

    @Test
    @TestId(id="TAF_DDT_Func_012")
    public void sharedDataSourceFollowedByTransform(){
        TestDataSource<DataRecord> transformedSharedInstrumentDataSource = TafDataSources.transform(TafDataSources.shared(TafDataSources.fromCsv("data/instruments.csv")), new Transform());

        TafTestContext.getContext().addDataSource("transformedSharedInstrumentDataSource", transformedSharedInstrumentDataSource);

        TestStepFlow flow = flow("Transformed Shared Data Source Flow")
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_INCREMENT_TS))
                .withDataSources(dataSource("transformedSharedInstrumentDataSource"))
                .build();

        TestStepFlow verifyFlow = flow("Verify Transformed Shared Data Source Flow")
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_VERIFY_TS))
                .addTestStep(annotatedMethod(dataDrivenFuncTestSteps, DataDrivenFuncTestSteps.TRANSFORMED_SHARED_RESET_TS))
                .build();

        TestScenario scenario = scenario().addFlow(flow).withDefaultVusers(5).build();
        runner().build().start(scenario);
        scenario = scenario().addFlow(verifyFlow).build();
        runner().build().start(scenario);
        TafTestContext.getContext().removeDataSource("transformedSharedInstrumentDataSource");
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestId(id = "TAF_DDT_Func_009")
    public void transformStrategyFromOneDataSourceToAnother() throws InterruptedException {

        TestDataSource<DataRecord> fruitDataSourceWithCyclic = TafDataSources.cyclic(fruitDataSource);

        TafTestContext.getContext().addDataSource("fruitDataSourceWithCyclic", fruitDataSourceWithCyclic);
        TafTestContext.getContext().addDataSource("occupationDataSource", occupationDataSource);

        TestStepFlow sharedToRepeat = flow("SharedToRepeat").addTestStep(annotatedMethod(dataDrivenFuncTestSteps, "verifyCorrectExecution"))
                .withDataSources(dataSource("fruitDataSourceWithCyclic"), dataSource("occupationDataSource")).build();

        TestStepFlow numberOfExecutions = flow("NumberOfExecutions").addTestStep(annotatedMethod(dataDrivenFuncTestSteps, "verifyCorrectExecutionResult"))
                .build();

        TestScenario scenario = scenario("Result").addFlow(sharedToRepeat).addFlow(numberOfExecutions).build();
        runner().build().start(scenario);

    }

    class Transform implements Function<DataRecord, DataRecord> {

        @Override
        public DataRecord apply(DataRecord input) {
            Map<String, Object> users = Maps.newHashMap();
            users.putAll(input.getAllFields());
            users.put("name", "Joe Bloggs");
            return new DataRecordImpl(users);
        }
    }

}

package com.ericsson.cifwk.taf.taffit.test.cases.datadriven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.fromTestStepResult;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;

public class KeyReplacementTest extends TafTestBase {
    private static final Logger LOGGER = getLogger(KeyReplacementTest.class);

    @Inject
    private TestContext context;

    @Test
    public void testKeyReplacement() {
        final TestScenario scenario = scenario()
                .addFlow(flow("")
                        .addTestStep(annotatedMethod(this, "dataRecord"))
                        .addTestStep(annotatedMethod(this, "dataRecordExtension"))
                        .addTestStep(annotatedMethod(this, "singleParameters"))
                        .addTestStep(annotatedMethod(this, "putDataIntoMap"))
                        .addTestStep(annotatedMethod(this, "readMapIn").withParameter("map",
                                fromTestStepResult("putDataIntoMap")))
                        .withDataSources(dataSource("pibParameters")))
                .build();

        runner()
                .withListener(new LoggingScenarioListener())
                .build()
                .start(scenario);
    }

    @TestStep(id = "dataRecordExtension")
    public void dataRecordExtension(@Input("pibParameters") final PibParameters pib) {
        LOGGER.info("Param value is: {}", pib.getParamValue());
        assertThat(pib.getParamValue()).matches(pib.getExpectedParamValue());
    }

    public interface PibParameters extends DataRecord {
        String getParamValue();

        String getExpectedParamValue();
    }

    @TestStep(id = "singleParameters")
    public void singleParameters(@Input("paramValue") final String paramValue,
            @Output("expectedParamValue") final String expectedParamValue) {
        assertThat(paramValue).matches(expectedParamValue);
    }

    @TestStep(id = "putDataIntoMap")
    public Map<String, String> putDataIntoMap(@Input("paramValue") final String paramValue,
            @Output("expectedParamValue") final String expectedParamValue) {
        System.setProperty("data", "my data");
        final Map<String, String> data = new HashMap<>();
        data.put("paramValue", paramValue);
        data.put("expectedParamValue", expectedParamValue);
        data.put("property", "${data}");
        return data;
    }

    @TestStep(id = "readMapIn")
    public void readMapIn(@Input("map") final Map<String, String> map) {
        assertThat(map).containsEntry("property", "my data");
        assertThat(map.get("paramValue")).matches(map.get("expectedParamValue"));
        buildCommandTemplateConstantsFromDs("pibParameters");
    }

    @TestStep(id = "dataRecord")
    public void dataRecord(@Input("pibParameters") final DataRecord dataRecord) {
        assertThat(dataRecord.getFieldValue("paramValue").toString()).matches(dataRecord.getFieldValue
                ("expectedParamValue").toString());
    }

    private Map<String, String> buildCommandTemplateConstantsFromDs(final String dsTestDataSource) {
        final TestDataSource<DataRecord> ds = TafDataSources.fromTafDataProvider(dsTestDataSource);
        final Map<String, String> templateParams = new HashMap<String, String>();
        for (final DataRecord record : ds) {
            final Map<String, Object> allFields = record.getAllFields();
                for (final Map.Entry<String, Object> entry : allFields.entrySet()) {
                    LOGGER.info("Putting {} into the map", entry);
                    templateParams.put(entry.getKey(), entry.getValue().toString());
                }
                break;

        }
        return templateParams;
    }
}

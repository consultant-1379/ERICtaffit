package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.FileInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.cifwk.taf.Constants.UISDK_VAPP_HOST;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromClass;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by ekonsla on 25/01/2016.
 */
public class RemoteObjectHandlerTestScenarios {

    private RemoteObjectHandler remoteObjectHandler;

    @BeforeMethod
    public void setUp() throws IOException {
        Host gatewayHost = TafDataHandler.findHost().withHostname(UISDK_VAPP_HOST).get();
        User user = new User("root", "shroot", UserType.ADMIN);
        remoteObjectHandler = new RemoteObjectHandler(gatewayHost, user);
    }

    @Test
    @TestId(id = "TAF_Remote_Object_Handler_Func_001")
    public void testRemoteFileChangePermissons() {
        TestDataSource<DataRecord> dataSource = fromClass(ChangePermissionsDatasource.class);
        TafTestContext.getContext().addDataSource("records", dataSource);

        TestScenario scenario = scenario("RemoteScriptExecutionScenario")
                .addFlow(flow("ExecuteRemoteScript")
                        .addTestStep(annotatedMethod(this, "create-file-on-remote-host"))
                        .addTestStep(annotatedMethod(this, "change-permissions-of-remote-file"))
                        .addTestStep(annotatedMethod(this, "delete-file-on-remote-host"))
                        .withDataSources(dataSource("records"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @Test
    @TestId(id = "TAF_Remote_Object_Handler_Func_002")
    public void testGetHomeDir() {
        TestScenario scenario = scenario("GetHomeDirScenario")
                .addFlow(flow("GetHomeDir")
                        .addTestStep(annotatedMethod(this, "get-home-dir"))
                        .build())
                .build();
        runner().build().start(scenario);
    }

    @TestStep(id = "get-home-dir")
    public void getHomeDir(){
        assertThat(remoteObjectHandler.getHomeDir(), is("/root/"));
    }

    @TestStep(id = "create-file-on-remote-host")
    public void createRemoteFile(@Input("tmpDirectoryLocation") String tmpLocation, @Input("fileName") String filename){
        String remoteFile = tmpLocation + filename;
        remoteObjectHandler.createRemoteFile(remoteFile, 100l, "1");
        assertThat(remoteObjectHandler.remoteFileExists(remoteFile), is(true));
    }

    @TestStep(id = "change-permissions-of-remote-file")
    public  void changePermissionsOfTheFile(@Input("tmpDirectoryLocation") String tmpLocation, @Input("fileName") String filename){
        String remoteFile = tmpLocation + filename;
        remoteObjectHandler.changeMod(remoteFile, 775);
        FileInfo lsstat = remoteObjectHandler.getInfo(remoteFile);
        assertThat(lsstat.getPermissionsOctal(), is(775));

        remoteObjectHandler.changeMod(remoteFile, 777);
        lsstat = remoteObjectHandler.getInfo(remoteFile);
        assertThat(lsstat.getPermissionsOctal(), is(777));
    }

    @TestStep(id = "delete-file-on-remote-host")
    public void deleteRemoteFile(@Input("tmpDirectoryLocation") String tmpLocation, @Input("fileName") String filename){
        String remoteFile = tmpLocation + filename;
        remoteObjectHandler.deleteRemoteFile(remoteFile);
        assertThat(remoteObjectHandler.remoteFileExists(remoteFile), is(false));
    }

    public static class ChangePermissionsDatasource {
        @DataSource
        public List<Map<String, Object>> records() throws IOException {

            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, Object> record = new HashMap<>();
            record.put("tmpDirectoryLocation", "/var/tmp/");
            record.put("fileName", randomAlphanumeric(10));
            records.add(record);

            return records;
        }
    }
}

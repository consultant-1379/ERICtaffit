package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.utils.ssh.J2SshFileCopy;
import com.ericsson.nms.host.HostConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertThat;
import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertTrue;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by ekonsla on 31/08/2016.
 */
public class J2SshFileCopyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(J2SshFileCopyTest.class);

    protected static Host ms1;
    protected static String remoteTmpDir = "/tmp/";
    protected static String fileSeparator = System.getProperty("file.separator");
    protected static String localTmpDir = System.getProperty("java.io.tmpdir");
    protected String linuxFileSeparator = "/";
    protected String localFile1Name = "localFileForCopy1.txt";
    protected String rootFolderForCopy = "localFolderForCopy" + fileSeparator;
    protected String randomTmpDir = UUID.randomUUID() + fileSeparator;
    protected String randomBackTmpDir = UUID.randomUUID() + fileSeparator;
    protected String remoteRandomFolder = remoteTmpDir + UUID.randomUUID() + linuxFileSeparator;

    @TestId(id = "TAF_J2SshFileCopy_Func_001", title = "Create and delete a file on a remote server using J2SshFileCopy")
    @Test
    public void copyLocalFileToRemoteAndBack() throws IOException {
        String localFile = localTmpDir + randomTmpDir + rootFolderForCopy + localFile1Name;
        String remoteFile = remoteRandomFolder + localFile1Name;
        String receivedBackLocalFile = localTmpDir + randomBackTmpDir + rootFolderForCopy + localFile1Name;;

        // All folders from the path will be automatically created
        createFileWithContent(localFile, "Some content");

        ms1 = HostConfigurator.getMS();

        J2SshFileCopy.putFile(localFile, remoteFile,
                ms1.getIp(), ms1.getUser(UserType.ADMIN), ms1.getPass(UserType.ADMIN),
                ms1.getPort().get(Ports.SSH));

        J2SshFileCopy.getFile(remoteFile, receivedBackLocalFile,
                ms1.getIp(), ms1.getUser(UserType.ADMIN), ms1.getPass(UserType.ADMIN),
                ms1.getPort().get(Ports.SSH));

        // All folders from the path will be automatically created
        File received = new File(receivedBackLocalFile);
        assertTrue(received.exists());
        assertThat(readFile(received), is("Some content"));
    }

    private File createFileWithContent(String localFileLocation, String content) throws IOException {
        File file = new File(localFileLocation);
        Files.writeFile(content, file);
        return file;
    }

    private String readFile(File file) throws IOException {
        String result = Files.readFile(file);
        return result.trim();
    }
}

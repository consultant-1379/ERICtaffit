package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.nms.host.HostConfigurator;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertTrue;

/**
 * Created by ejohlyn on 2/22/16.
 */
public class RemoteObjectHandlerHelper extends TafTestBase {

    protected static String content = "Default file content";
    protected static String remoteTmpDir = "/tmp/tmp/";
    protected static String localTmpDir = System.getProperty("java.io.tmpdir");
    protected static String fileSeparator = System.getProperty("file.separator");
    protected static String remoteKeyFilePath = "/root/.ssh/vm_private_key";
    protected static String localKeyFileName = "vm_private_key";
    protected static String localKeyFilePath;

    protected static RemoteObjectHandler remoteObjectHandler;
    protected static RemoteObjectHandler remoteObjectHandlerWithKey;
    protected static RemoteObjectHandler netsimRemoteObjectHandler;

    protected static Host ms1;
    protected static Host visinamingsbHost;
    protected static Host netsim;

    protected String randomTmpDir = RandomStringUtils.randomAlphanumeric(6) + fileSeparator;
    protected String rootFolderForCopy = "localFolderForCopy";
    protected String localFolderForCopy = "";
    protected String linuxFileSeparator = "/";
    protected String remoteRandomFolder = remoteTmpDir + "taffit_" + RandomStringUtils.randomAlphanumeric(6) + linuxFileSeparator;
    protected String subFolderForCopy = "localSubfolderForCopy";
    protected String subFolderRemoteForCopy = "/localSubfolderForCopy";
    protected String localFile1Name = "localFileForCopy1.txt";
    protected String localFile2Name = "localFileForCopy2.txt";
    protected String remoteFile1Name = "/localFileForCopy1.txt";
    protected String remoteFile2Name = "/localFileForCopy2.txt";
    protected String genericFile1Name = "localFileForCopy1.txt";
    protected String file1CheckSum = "";
    protected String file2CheckSum = "";
    protected String remoteFileCheckSum = "036c0c5b41328a203d5679ff965dd0cc";

    protected String createRemoteFileTestFilePath = remoteRandomFolder + "/CreateRemoteFile.txt";

    protected final String remoteComparisonBaseFileName = "sameFileForCompare.txt";
    protected final String remoteComparisonDiffFileName = "differentFile.txt";

    protected final String compareTestInitialFileLocation = remoteRandomFolder + "RemoteCompareTestInitialFile.txt";
    protected final String compareTestComparedFileLocation = remoteRandomFolder + "RemoteCompareTestSimilarFile.txt";
    protected final String compareTestRemoteDifferentFile = remoteRandomFolder + "RemoteCompareTestDifferentFile.txt";

    protected String remoteToRemoteTestInitialFileLocation = remoteRandomFolder + "RemoteFileToRemote.txt";
    protected String remoteToRemoteTestReceivedFileLocation = remoteRandomFolder + "RemoteFileReceivedFromRemote.txt";


    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteObjectHandlerHelper.class);

    @BeforeClass
    public void setUp() {
        //Needed for windows machines
        if (!localTmpDir.endsWith(fileSeparator)) {
            localTmpDir = localTmpDir + fileSeparator;
        }
        subFolderForCopy = fileSeparator + subFolderForCopy;
        localFile1Name = fileSeparator + localFile1Name;
        localFile2Name = fileSeparator + localFile2Name;

        localKeyFilePath = localTmpDir + localKeyFileName;
        localFolderForCopy = localTmpDir + randomTmpDir + rootFolderForCopy;

        visinamingsbHost = HostConfigurator.getSouthboundNamingServiceHost();
        ms1 = HostConfigurator.getMS();
        netsim = HostConfigurator.getAllNetsimHosts().get(0);

        remoteObjectHandler = new RemoteObjectHandler(ms1);
        remoteObjectHandlerWithKey = new RemoteObjectHandler(visinamingsbHost);
        netsimRemoteObjectHandler = new RemoteObjectHandler(netsim);
        assertTrue("Copy ssh private key to local to be used in testcases", remoteObjectHandler.copyRemoteFileToLocal(remoteKeyFilePath, localKeyFilePath));

        createLocalFilesRequiredForTesting();
        createRemoteFilesRequiredForTesting();

    }

    protected void sendPassword(CLICommandHelper commandHelper) {
        Shell shell = commandHelper.getShell();
        shell.writeln(visinamingsbHost.getPass());
        shell.expect(Pattern.compile("(?s).*[#>$]\\s*|(?s).*"));
    }

    protected void createLocalFilesRequiredForTesting() {
        try {
            new File(localFolderForCopy + subFolderForCopy).mkdirs();
            File file1 = new File(localFolderForCopy + localFile1Name);
            Files.write(file1.toPath(), "content file1".getBytes(), StandardOpenOption.CREATE);
            file1CheckSum = getCheckSum(file1.toString());
            File file2 = new File(localFolderForCopy + subFolderForCopy + localFile2Name);
            Files.write(file2.toPath(), "content file2".getBytes(), StandardOpenOption.CREATE);
            file2CheckSum = getCheckSum(file2.toString());

        } catch (IOException e) {
            LOGGER.error("Error creating temp folder", e);
        }
    }

    protected void createRemoteFilesRequiredForTesting() {
        File tempFileSame = createLocalDummyFile(localTmpDir + randomTmpDir + remoteComparisonBaseFileName, content);
        File tempFileDiff = createLocalDummyFile(localTmpDir + randomTmpDir + remoteComparisonDiffFileName, content + "plus extra content to differentiate");

        remoteObjectHandler.createRemoteDirectory(remoteRandomFolder);
        assertTrue("Copying comparison file #1 to remote",
                remoteObjectHandler.copyLocalFileToRemote(tempFileSame.toString(), compareTestInitialFileLocation, localTmpDir + randomTmpDir));
        assertTrue("Copying comparison file #2 to remote",
                remoteObjectHandler.copyLocalFileToRemote(tempFileDiff.toString(), compareTestRemoteDifferentFile, localTmpDir + randomTmpDir));
        assertTrue("Copying comparison file #3 to remote",
                remoteObjectHandler.copyLocalFileToRemote(tempFileSame.toString(), compareTestComparedFileLocation, localTmpDir + randomTmpDir));

        assertTrue("Create remote file for remote to remote copy test",
                remoteObjectHandler.createRemoteFile(remoteToRemoteTestInitialFileLocation, 100l, "1"));

        remoteObjectHandlerWithKey.createRemoteDirectoryWithSshKeyFile(remoteRandomFolder, localKeyFilePath);
        assertTrue("Copying comparison file #1 to remote",
                remoteObjectHandlerWithKey.copyLocalFileToRemoteWithSshKeyFile(tempFileSame.toString(), compareTestInitialFileLocation, localTmpDir + randomTmpDir, localKeyFilePath));
        assertTrue("Copying comparison file #2 to remote",
                remoteObjectHandlerWithKey.copyLocalFileToRemoteWithSshKeyFile(tempFileDiff.toString(), compareTestRemoteDifferentFile, localTmpDir + randomTmpDir, localKeyFilePath));
        assertTrue("Copying comparison file #3 to remote",
                remoteObjectHandlerWithKey.copyLocalFileToRemoteWithSshKeyFile(tempFileSame.toString(), compareTestComparedFileLocation, localTmpDir + randomTmpDir, localKeyFilePath));

        assertTrue("Create remote file for remote to remote copy test",
                remoteObjectHandlerWithKey.createRemoteFileWithSshKeyFile(remoteToRemoteTestInitialFileLocation, 100l, "1", localKeyFilePath));
    }

    protected File createLocalDummyFile(String filePath, String content) {
        File temp = new File(filePath);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(temp));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Error creating dummy file locally", e);
        }
        return temp;
    }

    protected String getCheckSum(String filePath) {
        MessageDigest md = null;
        String response = "";
        try {
            md = MessageDigest.getInstance("MD5");

            FileInputStream fis = new FileInputStream(filePath);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            ;
            byte[] mdbytes = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            response = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Problem occurred while calculating checksum", e);
        } catch (IOException e) {
            LOGGER.error("Problem occurred while calculating checksum", e);
        }
        return response;
    }
}

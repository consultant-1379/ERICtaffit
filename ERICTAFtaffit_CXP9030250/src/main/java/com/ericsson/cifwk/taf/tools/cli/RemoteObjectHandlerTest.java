/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;
import com.ericsson.cifwk.taf.utils.FileStructure;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class RemoteObjectHandlerTest extends RemoteObjectHandlerHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteObjectHandlerTest.class);

    @TestId(id = "TAF_Remote_Object_Handler_Func_003", title = "Create and delete a file on a remote server")
    @Test
    public void createAndDeleteRemoteFileTest() {
        if (remoteObjectHandler.remoteFileExists(createRemoteFileTestFilePath)) {
            remoteObjectHandler.deleteRemoteFile(createRemoteFileTestFilePath);
        }
        boolean createdRemoteFile = remoteObjectHandler.createRemoteFile(createRemoteFileTestFilePath, 100L, "1");
        assertTrue("Verifying remote file was created", createdRemoteFile);
        FileStructure fs = remoteObjectHandler.getRemoteFileInformation(createRemoteFileTestFilePath);
        assertTrue("Verifying checksum of remote file is correct", fs.md5.matches(remoteFileCheckSum));
        assertTrue("Deleting remote file", remoteObjectHandler.deleteRemoteFile(createRemoteFileTestFilePath));
        assertFalse("Verifying that the remote file has been deleted", remoteObjectHandler.remoteFileExists(createRemoteFileTestFilePath));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_004", title = "Copy a local file to a remote server and back again")
    @Test
    public void copyLocalFileToRemoteAndBack() {
        String localFileLocation = localTmpDir + randomTmpDir + rootFolderForCopy + localFile1Name;
        String remoteFileLocation = remoteRandomFolder + localFile1Name;

        if (remoteObjectHandler.remoteFileExists(remoteFileLocation)) {
            remoteObjectHandler.deleteRemoteFile(remoteFileLocation);
        }

        boolean copyLocalFileToRemote = remoteObjectHandler.copyLocalFileToRemote(localFileLocation, remoteFileLocation, localTmpDir + randomTmpDir);
        assertTrue("Verifying local file copied to remote", copyLocalFileToRemote);
        boolean remoteFileInformationMatches = remoteObjectHandler.getRemoteFileInformation(remoteFileLocation).md5.equals(file1CheckSum);
        assertTrue("Verifying that checksum of the remote file matches expected", remoteFileInformationMatches);

        String localFileCopiedFromRemote = localTmpDir + randomTmpDir + "copiedFromRemote.txt";
        boolean copyRemoteFileToLocal = remoteObjectHandler.copyRemoteFileToLocal(remoteFileLocation, localFileCopiedFromRemote);
        assertTrue("Verifying the remote file copied back to local", copyRemoteFileToLocal);
        assertTrue("Verifying that the checksum of the file copied back to local matches what is expected",
                getCheckSum(localFileCopiedFromRemote).equals(file1CheckSum));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_005", title = "Copy a local directory to a remote server and back again")
    @Test
    public void copyLocalDirToRemoteAndBack() {
        boolean localDirCopiedToRemote = remoteObjectHandler.copyLocalDirToRemote(localFolderForCopy, remoteRandomFolder);
        assertTrue("Verifying local directory copied to remote", localDirCopiedToRemote);

        assertTrue("Verifying the remote file exists",
                remoteObjectHandler.remoteFileExists(remoteRandomFolder + rootFolderForCopy + remoteFile1Name));
        assertTrue("Verifying the remote file exists in the subfolder",
                remoteObjectHandler.remoteFileExists(remoteRandomFolder + rootFolderForCopy + subFolderRemoteForCopy + remoteFile2Name));

        FileStructure fileStructureCopy1 = remoteObjectHandler.getRemoteFileInformation(remoteRandomFolder + rootFolderForCopy + remoteFile1Name);
        FileStructure fileStructureCopy2 = remoteObjectHandler.getRemoteFileInformation(remoteRandomFolder + rootFolderForCopy + subFolderRemoteForCopy +
                remoteFile2Name);
        assertTrue("Verifying checksum of remote file matches what is expected", fileStructureCopy1.md5.matches(file1CheckSum));
        assertTrue("Verifying checksum of remote file in the subfolder matches what is expected", fileStructureCopy2.md5.matches(file2CheckSum));

        String localFolderAfterCopyBack = localTmpDir + randomTmpDir + "copiedBack" + fileSeparator;
        try {
            Files.createDirectory(Paths.get(localFolderAfterCopyBack));
        } catch (IOException e) {
            LOGGER.error("Failed to create target folder to copy remote directory into", e);
        }
        boolean copyRemoteDirToLocal = remoteObjectHandler.copyRemoteDirToLocal(remoteRandomFolder, localFolderAfterCopyBack);
        assertTrue("Verifying remote directory copied back to local", copyRemoteDirToLocal);
        File file1 = new File(localFolderAfterCopyBack + rootFolderForCopy + localFile1Name);
        File file2 = new File(localFolderAfterCopyBack + rootFolderForCopy + subFolderForCopy + localFile2Name);
        assertTrue("Verify file exists when directory is copied back to local", file1.exists());
        assertTrue("Verify file in the subfolder exists when directory is copied back to local", file2.exists());
        assertTrue("Verify checksum of file that was copied back", getCheckSum(file1.toString()).matches(file1CheckSum));
        assertTrue("Verify checksum of file in subfolder that was copied back", getCheckSum(file2.toString()).matches(file2CheckSum));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_006", title = "Get the details of a remote file")
    @Test
    public void getRemoteFileInformationTest() {

        final FileStructure fileStructureForCompare = remoteObjectHandler.getRemoteFileInformation(compareTestInitialFileLocation);
        final FileStructure fileStructureForCompare1 = remoteObjectHandler.getRemoteFileInformation(compareTestComparedFileLocation);
        final FileStructure differentFileStructure = remoteObjectHandler.getRemoteFileInformation(compareTestRemoteDifferentFile);

        assertFalse("Verifying remote file sizes are different", fileStructureForCompare.filesize.equals(differentFileStructure.filesize));
        assertFalse("Verifying remote checksums are different", fileStructureForCompare.md5.equals(differentFileStructure.md5));
        assertTrue("Verifying remote file sizes are the same", fileStructureForCompare.filesize.equals(fileStructureForCompare1.filesize));
        assertTrue("Verifying remote file checksums are the same", fileStructureForCompare.md5.equals(fileStructureForCompare1.md5));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_007", title = "Copy a file from one remote server to another remote server")
    @Test
    public void copyRemoteToRemoteTest() {
        boolean copyRemoteToRemote = remoteObjectHandler.copyRemoteFileToRemote(ms1, remoteToRemoteTestInitialFileLocation, netsim,
                remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying remote file copied to remote", copyRemoteToRemote);
        boolean remoteFileExists = netsimRemoteObjectHandler.remoteFileExists(remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying destination file exists after copy", remoteFileExists);
        FileStructure fs1 = remoteObjectHandler.getRemoteFileInformation(remoteToRemoteTestInitialFileLocation);
        FileStructure fs2 = netsimRemoteObjectHandler.getRemoteFileInformation(remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying checksum after copy", fs1.md5.equals(fs2.md5));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_016", title = "Copy a file from one remote server to another remote server using CLI")
    @Test
    public void copyRemoteToRemoteWithCliTest() {
        boolean copyRemoteToRemote = remoteObjectHandler.copyRemoteFileToRemoteWithCli(ms1, remoteToRemoteTestInitialFileLocation, ms1,
                remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying remote file copied to remote", copyRemoteToRemote);

        boolean remoteFileExists = remoteObjectHandler.remoteFileExists(remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying destination file exists after copy", remoteFileExists);

        FileStructure fs1 = remoteObjectHandler.getRemoteFileInformation(remoteToRemoteTestInitialFileLocation);
        FileStructure fs2 = remoteObjectHandler.getRemoteFileInformation(remoteToRemoteTestReceivedFileLocation);
        assertTrue("Verifying checksum after copy", fs1.md5.equals(fs2.md5));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_017", title = "Ensure copyRemoteToRemoteWithCLi gets called if other remoteToRemote methods fail")
    @Test
    public void copyRemoteToRemoteCallsWithCli() {
        String dummyPath = remoteRandomFolder +"IDoNotExist.txt";
        RemoteObjectHandler spyRoh = spy(remoteObjectHandler);
        spyRoh.copyRemoteFileToRemote(ms1, dummyPath, ms1, dummyPath);
        verify(spyRoh).copyRemoteFileToRemoteWithCli(ms1, dummyPath, ms1, dummyPath);
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_008", title = "Compare files on a remote server")
    @Test
    public void compareRemoteFilesTest() {
        boolean comparedRemoteFilesTrue = remoteObjectHandler.compareRemoteFiles(ms1, compareTestInitialFileLocation, ms1,
                compareTestComparedFileLocation);
        assertTrue("Verifying remote files are same", comparedRemoteFilesTrue);

        boolean comparedRemoteFilesFalse = remoteObjectHandler.compareRemoteFiles(ms1, compareTestInitialFileLocation, ms1,
                compareTestRemoteDifferentFile);
        assertFalse("Verifying remote files are different", comparedRemoteFilesFalse);
                remoteObjectHandler.compareRemoteFiles(ms1, compareTestInitialFileLocation, ms1,
                        compareTestRemoteDifferentFile);
    }


    @TestId(id = "TAF_Remote_Object_Handler_Func_009", title = "Confirm all SSH connections are closed after object transfer is complete")
    @Test
    public void checkRemoteObjectHandlerClosesSshConnectionsTest() {
        String remoteFileLocation = remoteTmpDir + remoteToRemoteTestInitialFileLocation;
        try {
            for (int i = 0; i < 12; i++) {
                remoteObjectHandler.createRemoteFile(remoteFileLocation, 100L, "1");
                remoteObjectHandler.deleteRemoteFile(remoteFileLocation);
            }
        } catch (JSchCLIToolException e) {
            fail("JSch exception occurred connecting to host due to too many open connections. Test failed with error " + e);
        } catch (RuntimeException e) {
            fail("JSch exception occurred connecting to host, this is likely due to too many open connections. Test failed with error " + e);
        } catch (Exception e) {
            fail("Error occurred connecting to host. Test failed with error " + e);
        } finally {
            if (remoteObjectHandler.remoteFileExists(remoteFileLocation)) {
                remoteObjectHandler.deleteRemoteFile(remoteFileLocation);
            }
        }
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_010", title = "Confirm Remote File Search for existing files with wildcard")
    @Test
    public void remoteFilePatternExistsTest() {
        Assert.assertTrue(remoteObjectHandler.createRemoteFile(remoteRandomFolder + "wildcardfile1.log", 1000L, ""));
        Assert.assertTrue(remoteObjectHandler.createRemoteFile(remoteRandomFolder + "wildcardfile2.log", 1000L, ""));
        Assert.assertTrue(remoteObjectHandler.createRemoteFile(remoteRandomFolder + "wildcardfile3.log", 1000L, ""));
        List<String> files = remoteObjectHandler.remoteFileExistsWithWildCard(remoteRandomFolder+"wildcardfile*.log");
        assertEquals(files.size(), 3, "Correct Number of Files Not Found");
        files = remoteObjectHandler.remoteFileExistsWithWildCard(remoteRandomFolder+"doesnotexist*.log");
        assertTrue("Files found with wildcard pattern that should not exist", files.isEmpty());
    }

    @AfterClass
    public void tearDown() {
        boolean deleteRemoteFile = netsimRemoteObjectHandler.deleteRemoteFolder(remoteTmpDir);
        assertTrue("Remote temp folder on netsim was not deleted after execution", deleteRemoteFile);
        deleteRemoteFile = remoteObjectHandler.deleteRemoteFolder(remoteTmpDir);
        assertTrue("Remote temp folder on ms1 was not deleted after execution", deleteRemoteFile);
        FileUtils.deleteQuietly(new File(localTmpDir + randomTmpDir));
        assertFalse("Local temp folder was not deleted", Files.exists(Paths.get(localTmpDir + randomTmpDir)));
    }
}

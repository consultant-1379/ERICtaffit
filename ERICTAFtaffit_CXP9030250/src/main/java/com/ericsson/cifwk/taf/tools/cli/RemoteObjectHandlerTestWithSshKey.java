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
import com.ericsson.cifwk.taf.utils.FileStructure;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafHamcrestAsserts.assertTrue;

public class RemoteObjectHandlerTestWithSshKey extends RemoteObjectHandlerHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteObjectHandlerTestWithSshKey.class);

    @TestId(id = "TAF_Remote_Object_Handler_Func_010", title = "Create and delete a file on a remote server using ssh key")
    @Test
    public void createAndDeleteRemoteFileTest() {
        if (remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(createRemoteFileTestFilePath, localKeyFilePath)) {
            remoteObjectHandlerWithKey.deleteRemoteFileWithSshKeyFile(createRemoteFileTestFilePath, localKeyFilePath);
        }
        assertTrue("Creating remote file with ssh key",
                remoteObjectHandlerWithKey.createRemoteFileWithSshKeyFile(createRemoteFileTestFilePath, 100l, "1", localKeyFilePath));
        FileStructure fs = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(createRemoteFileTestFilePath, localKeyFilePath);
        assertTrue("Verifying checksum of remote file is correct", fs.md5.matches(remoteFileCheckSum));
        assertTrue("Deleting remote file with ssh key", remoteObjectHandlerWithKey.deleteRemoteFileWithSshKeyFile(createRemoteFileTestFilePath, localKeyFilePath));
        assertFalse("Verifying that the remote file has been deleted", remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(createRemoteFileTestFilePath, localKeyFilePath));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_011", title = "Copy a local file to a remote server and back again using ssh key")
    @Test
    public void copyLocalFileToRemoteAndBack() {
        String localFileLocation = localTmpDir + randomTmpDir+ rootFolderForCopy + localFile1Name;
        String remoteFileLocation = remoteRandomFolder + genericFile1Name;

        if (remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(remoteFileLocation, localKeyFilePath)) {
            remoteObjectHandlerWithKey.deleteRemoteFileWithSshKeyFile(remoteFileLocation, localKeyFilePath);
        }

        assertTrue("Copying local file to remote with ssh key",
                remoteObjectHandlerWithKey.copyLocalFileToRemoteWithSshKeyFile(localFileLocation, remoteFileLocation, localTmpDir + randomTmpDir, localKeyFilePath));
        assertTrue("Verifying that checksum of the remote file matches expected",
                remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(remoteFileLocation, localKeyFilePath).md5.equals(file1CheckSum));

        String localFileCopiedFromRemote = localTmpDir + randomTmpDir + "copiedFromRemote.txt";
        assertTrue("Copying the remote file back to local with ssh key",
                remoteObjectHandlerWithKey.copyRemoteFileToLocalWithSshKeyFile(remoteFileLocation, localFileCopiedFromRemote, localKeyFilePath));
        assertTrue("Verifying that the checksum of the file copied back to local matches what is expected",
                getCheckSum(localFileCopiedFromRemote).equals(file1CheckSum));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_012", title = "Copy a local directory to a remote server and back again using ssh key")
    @Test
    public void copyLocalDirToRemoteAndBack() {
        assertTrue("Copying local directory to remote with ssh key",
                remoteObjectHandlerWithKey.copyLocalDirToRemoteWithSshKeyFile(localFolderForCopy, remoteRandomFolder, localKeyFilePath));

        assertTrue("Verifying the remote file exists with ssh key",
                remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(remoteRandomFolder + rootFolderForCopy + remoteFile1Name, localKeyFilePath));
        assertTrue("Verifying the remote file exists in the subfolder with ssh key",
                remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(remoteRandomFolder + rootFolderForCopy + subFolderRemoteForCopy + remoteFile2Name, localKeyFilePath));

        FileStructure fileStructureCopy1 = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(remoteRandomFolder + rootFolderForCopy + remoteFile1Name, localKeyFilePath);
        FileStructure fileStructureCopy2 = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(remoteRandomFolder + rootFolderForCopy + subFolderRemoteForCopy +
                remoteFile2Name, localKeyFilePath);
        assertTrue("Verifying checksum of remote file matches what is expected", fileStructureCopy1.md5.matches(file1CheckSum));
        assertTrue("Verifying checksum of remote file in the subfolder matches what is expected", fileStructureCopy2.md5.matches(file2CheckSum));

        String localFolderAfterCopyBack = localTmpDir + randomTmpDir + "copiedBackSshKey" + fileSeparator;
        try {
            Files.createDirectory(Paths.get(localFolderAfterCopyBack));
        } catch (IOException e) {
            LOGGER.error("Failed to create target folder to copy remote directory into", e);
        }
        assertTrue("Copying remote directory back to local", remoteObjectHandlerWithKey.copyRemoteDirToLocal(remoteRandomFolder, localFolderAfterCopyBack, localKeyFilePath));
        File file1 = new File(localFolderAfterCopyBack + rootFolderForCopy + localFile1Name);
        File file2 = new File(localFolderAfterCopyBack + rootFolderForCopy + subFolderForCopy + localFile2Name);
        assertTrue("Verify file exists when directory is copied back to local", file1.exists());
        assertTrue("Verify file in the subfolder exists when directory is copied back to local", file2.exists());
        assertTrue("Verify checksum of file that was copied back", getCheckSum(file1.toString()).matches(file1CheckSum));
        assertTrue("Verify checksum of file in subfolder that was copied back", getCheckSum(file2.toString()).matches(file2CheckSum));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_013", title = "Get the details of a remote file using ssh key")
    @Test
    public void getRemoteFileInformationTest() {

        final FileStructure fileStructureForCompare = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(compareTestInitialFileLocation, localKeyFilePath);
        final FileStructure fileStructureForCompare1 = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(compareTestComparedFileLocation, localKeyFilePath);
        final FileStructure differentFileStructure = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(compareTestRemoteDifferentFile, localKeyFilePath);

        assertFalse("Verifying remote file sizes are different", fileStructureForCompare.filesize.equals(differentFileStructure.filesize));
        assertFalse("Verifying remote checksums are different", fileStructureForCompare.md5.equals(differentFileStructure.md5));
        assertTrue("Verifying remote file sizes are the same", fileStructureForCompare.filesize.equals(fileStructureForCompare1.filesize));
        assertTrue("Verifying remote file checksums are the same", fileStructureForCompare.md5.equals(fileStructureForCompare1.md5));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_014", title = "Copy a file from one remote server to another remote server using ssh key")
    @Test
    public void copyRemoteToRemoteTest() {
        assertTrue("Copying remote file to remote with ssh key",
                remoteObjectHandlerWithKey.copyRemoteFileToRemoteWithSshKeyFile(visinamingsbHost, remoteToRemoteTestInitialFileLocation, localKeyFilePath, visinamingsbHost,
                        remoteToRemoteTestReceivedFileLocation, localKeyFilePath));
        assertTrue("Verifying destination file exists after copy with ssh key",
                remoteObjectHandlerWithKey.remoteFileExistsWithSshKeyFile(remoteToRemoteTestReceivedFileLocation, localKeyFilePath));
        FileStructure fs1 = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(remoteToRemoteTestInitialFileLocation, localKeyFilePath);
        FileStructure fs2 = remoteObjectHandlerWithKey.getRemoteFileInformationWithSshKeyFile(remoteToRemoteTestReceivedFileLocation, localKeyFilePath);
        assertTrue("Verifying checksum after copy", fs1.md5.equals(fs2.md5));
    }

    @TestId(id = "TAF_Remote_Object_Handler_Func_015", title = "Compare files on a remote server using ssh key")
    @Test
    public void compareRemoteFilesTest() {
        assertTrue("Comparing remote files with ssh key",
                remoteObjectHandlerWithKey.compareRemoteFilesWithSshKeyFile(visinamingsbHost, compareTestInitialFileLocation, localKeyFilePath, visinamingsbHost,
                        compareTestComparedFileLocation, localKeyFilePath));
        assertFalse("Verifying remote files are different with ssh key",
                remoteObjectHandlerWithKey.compareRemoteFilesWithSshKeyFile(visinamingsbHost, compareTestInitialFileLocation, localKeyFilePath, visinamingsbHost,
                        compareTestRemoteDifferentFile, localKeyFilePath));
    }

    @AfterClass
    public void tearDown() {
        assertTrue("Remote temp folder was not deleted after execution", remoteObjectHandlerWithKey.deleteRemoteFolderWithSshKeyFile(remoteRandomFolder, localKeyFilePath));

        FileUtils.deleteQuietly(new File(localTmpDir + randomTmpDir));
        assertFalse("Local temp folder was not deleted", Files.exists(Paths.get(localTmpDir + randomTmpDir)));
    }
}

package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.*;
import ch.supsi.fscli.backend.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LsServiceTest {

    private Filesystem filesystem;
    private LsService lsService;

    @BeforeEach
    void setUp() throws FileAlreadyExistsException, InvalidFilenameException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("TestFS");
        lsService = new LsService("List directory content", createFilesystemService);

        DirectoryINode root = filesystem.getRoot();
        filesystem.createFile(root, "fileRoot");

        DirectoryINode dir1 = filesystem.createDirectory(root, "dir1");
        filesystem.createFile(dir1, "fileInDir1");

        filesystem.createDirectory(root, "dirEmpty");
    }

    @Test
    void lsOnCurrentDirectoryDefaultsToDot() throws FileSystemException {
        String result = lsService.execute(new ArrayList<>(), List.of());

        assertTrue(result.contains("fileRoot"));
        assertTrue(result.contains("dir1"));
        assertTrue(result.contains("dirEmpty"));

        assertFalse(result.contains("fileInDir1"));
    }

    @Test
    void lsOnSpecificDirectory() throws FileSystemException {
        String targetDir = "/dir1";
        String result = lsService.execute(List.of(targetDir), List.of());

        assertTrue(result.contains("fileInDir1"));
        assertFalse(result.contains("fileRoot"));
    }

    @Test
    void lsWithInodeFlag() throws FileSystemException {
        INode fileRootNode = filesystem.getRoot().searchInFolder("fileRoot");
        long id = fileRootNode.getId();

        String result = lsService.execute(new ArrayList<>(), List.of("i"));

        assertTrue(result.contains("fileRoot"));
        assertTrue(result.contains(String.valueOf(id)));
    }

    @Test
    void lsMultipleDirectories() throws FileSystemException {
        List<String> operands = new ArrayList<>();
        operands.add(".");
        operands.add("/dir1");

        String result = lsService.execute(operands, List.of());

        assertTrue(result.contains("fileRoot"));
        assertTrue(result.contains("fileInDir1"));
        assertTrue(result.contains(":"));
    }

    @Test
    void lsThrowsExceptionForInvalidOption() {
        assertThrows(InvalidOptionException.class, () ->
                lsService.execute(new ArrayList<>(), List.of("z"))
        );
    }

    @Test
    void lsThrowsExceptionForTooManyFlags() {
        assertThrows(TooManyArgumentsException.class, () ->
                lsService.execute(new ArrayList<>(), List.of("i", "l"))
        );
    }

    @Test
    void lsThrowsExceptionForNonExistentPath() {
        String badPath = "/non/esiste";
        assertThrows(NoSuchFileOrDirectoryException.class, () ->
                lsService.execute(List.of(badPath), List.of())
        );
    }

    @Test
    void lsShowsSymlinkDestinationInRoot() throws FileSystemException {
        DirectoryINode root = filesystem.getRoot();
        INode targetNode = root.searchInFolder("fileRoot");
        filesystem.createSymlink(targetNode, root, "myLink");

        String result = lsService.execute(new ArrayList<>(), List.of());

        assertTrue(result.contains("myLink -> /fileRoot"));
    }

    @Test
    void lsShowsSymlinkToDeepDestination() throws FileSystemException {
        DirectoryINode root = filesystem.getRoot();
        DirectoryINode dir1 = (DirectoryINode) root.searchInFolder("dir1");
        FileINode deepFile = filesystem.createFile(dir1, "deepFile");

        filesystem.createSymlink(deepFile, root, "linkToDeep");

        String result = lsService.execute(new ArrayList<>(), List.of());

        assertTrue(result.contains("linkToDeep -> /dir1/deepFile"));
    }

    @Test
    void lsSymlinkWithInodeFlag() throws FileSystemException {
        DirectoryINode root = filesystem.getRoot();
        INode targetNode = root.searchInFolder("fileRoot");

        var symlink = filesystem.createSymlink(targetNode, root, "linkWithId");

        String result = lsService.execute(new ArrayList<>(), List.of("i"));

        assertTrue(result.contains(String.valueOf(symlink.getId())));
        assertTrue(result.contains("linkWithId -> /fileRoot"));
    }

    @Test
    void lsShowLinkContents() throws FileSystemException {
        DirectoryINode root = filesystem.getRoot();
        DirectoryINode folder = filesystem.createDirectory(root, "folder");
        long fileId = filesystem.createFile(folder,"file").getId();
        SymlinkINode folderLink = filesystem.createSymlink(folder, root, "folderLink");

        String result = lsService.execute(List.of("folderLink"),List.of());

        assertTrue(result.contains("file"));
    }
}

package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilesystemTest {

    private Filesystem fs;
    private DirectoryINode root;
    private DirectoryINode testDir;

    @BeforeEach
    public void setUp() throws FileSystemException {
        fs = new Filesystem("TestFS");
        root = fs.getRoot();
        testDir = fs.createDirectory(root, "testDir");
    }

    @Test
    public void testFilesystemCreation() {
        assertNotNull(fs);
        assertEquals("TestFS", fs.getFilesystemName());
        assertNotNull(fs.getRoot());
        assertEquals(fs.getRoot(), fs.getCurrentDir());
    }

    @Test
    public void testFilesystemCreationWithBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Filesystem(null));
        assertThrows(IllegalArgumentException.class, () -> new Filesystem(""));
        assertThrows(IllegalArgumentException.class, () -> new Filesystem("   "));
    }

    @Test
    public void testCreateFile() throws FileAlreadyExistsException, InvalidFilenameException {
        FileINode file = fs.createFile(testDir, "testFile.txt");

        assertNotNull(file);
        assertEquals(INodeType.FILE, file.getType());
        assertEquals(testDir, file.getParent());
        assertTrue(testDir.getChildren().containsKey("testFile.txt"));
    }

    @Test
    public void testCreateFileAlreadyExists() throws FileAlreadyExistsException, InvalidFilenameException {
        fs.createFile(testDir, "testFile.txt");
        assertThrows(FileAlreadyExistsException.class,
                () -> fs.createFile(testDir, "testFile.txt"));
    }

    @Test
    public void testCreateFileWithInvalidName() {
        assertThrows(InvalidFilenameException.class,
                () -> fs.createFile(testDir, "file/with/slash.txt"));
        assertThrows(InvalidFilenameException.class,
                () -> fs.createFile(testDir, "file*with*asterisk"));
    }

    @Test
    public void testCreateDirectory() throws FileAlreadyExistsException, InvalidFilenameException {
        DirectoryINode newDir = fs.createDirectory(testDir, "newDir");

        assertNotNull(newDir);
        assertEquals(INodeType.DIRECTORY, newDir.getType());
        assertEquals(testDir, newDir.getParent());
        assertTrue(testDir.getChildren().containsKey("newDir"));
    }

    @Test
    public void testCreateSymlink() throws FileAlreadyExistsException, InvalidFilenameException {
        FileINode targetFile = fs.createFile(testDir, "target.txt");
        SymlinkINode symlink = fs.createSymlink(targetFile, testDir, "linkToTarget");

        assertNotNull(symlink);
        assertEquals(INodeType.SYM_LINK, symlink.getType());
        assertEquals(targetFile, symlink.getDestination());
        assertTrue(testDir.getChildren().containsKey("linkToTarget"));
    }

    @Test
    public void testCreateHardlink() throws FileSystemException {
        FileINode targetFile = fs.createFile(testDir, "target.txt");
        DirectoryINode anotherDir = fs.createDirectory(root, "anotherDir");

        INode hardlink = fs.createHardlink(targetFile, anotherDir, "hardlinkToTarget");

        assertNotNull(hardlink);
        assertEquals(targetFile.getId(), hardlink.getId());
        assertEquals(2, targetFile.getReferenceCount());
        assertTrue(anotherDir.getChildren().containsKey("hardlinkToTarget"));
    }

    @Test
    public void testCreateHardlinkSameFileException() throws FileAlreadyExistsException, InvalidFilenameException {
        FileINode targetFile = fs.createFile(testDir, "target.txt");

        assertThrows(SameFileException.class,
                () -> fs.createHardlink(targetFile, testDir, "target.txt"));
    }

    @Test
    public void testCreateHardlinkNullDestination() {
        assertThrows(NoSuchFileOrDirectoryException.class,
                () -> fs.createHardlink(null, testDir, "link"));
    }

    @Test
    public void testRemoveFile() throws FileSystemException {
        fs.createFile(testDir, "fileToRemove.txt");

        fs.removeINode(testDir, "fileToRemove.txt");

        assertFalse(testDir.getChildren().containsKey("fileToRemove.txt"));
    }

    @Test
    public void testRemoveDirectory() throws FileSystemException {
        DirectoryINode emptyDir = fs.createDirectory(testDir, "emptyDir");

        fs.removeINode(testDir, "emptyDir");

        assertFalse(testDir.getChildren().containsKey("emptyDir"));
    }

    @Test
    public void testRemoveNonExistentFile() {
        assertThrows(NoSuchFileOrDirectoryException.class,
                () -> fs.removeINode(testDir, "nonExistentFile.txt"));
    }

    @Test
    public void testReferenceCountDecreaseOnRemove() throws FileSystemException {
        FileINode file = fs.createFile(testDir, "file.txt");
        DirectoryINode anotherDir = fs.createDirectory(root, "anotherDir");

        fs.createHardlink(file, anotherDir, "hardlink");
        assertEquals(2, file.getReferenceCount());

        fs.removeINode(testDir, "file.txt");
        assertEquals(1, file.getReferenceCount());
        assertFalse(testDir.getChildren().containsKey("file.txt"));
        assertTrue(anotherDir.getChildren().containsKey("hardlink"));
    }

    @Test
    public void testResolvePath() throws FileSystemException {
        var home = fs.createDirectory(root, "home");
        var user = fs.createDirectory(home, "user");
        var documents = fs.createDirectory(user, "Documents");
        var doc = fs.createFile(documents, "doc.txt");
        var pictures = fs.createDirectory(user, "Pictures");
        var album1 = fs.createDirectory(pictures, "album1");
        var img1 = fs.createFile(album1, "img1.jxl");

        SymlinkINode linkToDocuments = fs.createSymlink(documents, album1, "linkToDocuments");
        IHardLinkable linkToDoc = (IHardLinkable) fs.createHardlink(doc, pictures, "linkToDoc");

        assertEquals(linkToDoc.getId(), doc.getId());
        assertEquals(fs.resolvePath("/home/user/Pictures/linkToDoc").getId(), doc.getId());

        assertEquals(linkToDocuments.getDestination().getId(), documents.getId());
        assertEquals(((SymlinkINode)fs.resolvePath("/home/user/Pictures/album1/linkToDocuments")).getDestination().getId(), documents.getId());

        String absPathToDoc = "/home/user/Documents/doc.txt";
        String absPathToAlbum1 = "/home/user/Pictures/album1";
        String absPathToImg1 = "/home/user/Pictures/album1/img1.jxl";

        INode docResolved = fs.resolvePath(absPathToDoc);
        INode album1Resolved = fs.resolvePath(absPathToAlbum1);
        INode img1Resolved = fs.resolvePath(absPathToImg1);

        assertEquals(root.getId(), fs.resolvePath(Filesystem.ROOT).getId());
        assertEquals(docResolved.getId(), doc.getId());
        assertEquals(album1Resolved.getId(), album1.getId());
        assertEquals(img1Resolved.getId(), img1.getId());

        fs.setCurrentDir(pictures);
        String relPathToUser = "../../user";
        String relPathToDoc = "../Documents/doc.txt";
        String relPathToAlbum1ver1 = "album1/";
        String relPathToAlbum1ver2 = "album1";
        String relPathToAlbum1ver3 = "./album1/";
        String relPathToAlbum1ver4 = "./album1";

        assertEquals(fs.resolvePath(relPathToUser).getId(), user.getId());
        assertEquals(fs.resolvePath(relPathToDoc).getId(), doc.getId());
        assertEquals(fs.resolvePath(relPathToAlbum1ver1).getId(), album1.getId());
        assertEquals(fs.resolvePath(relPathToAlbum1ver2).getId(), album1.getId());
        assertEquals(fs.resolvePath(relPathToAlbum1ver3).getId(), album1.getId());
        assertEquals(fs.resolvePath(relPathToAlbum1ver4).getId(), album1.getId());
    }

    @Test
    public void generateListFromPath()  {
        String path1 = "/directory1/directory2/directory3/file.txt";
        List<String> list = Filesystem.generateFilesystemPath(path1);
        List<String> destinationList = Arrays.asList("/","directory1", "directory2", "directory3","file.txt");

        assertEquals(destinationList,list);
    }

    @Test
    public void generateListFromPathRoot()  {
        String path2 = "/";
        List<String> list = Filesystem.generateFilesystemPath(path2);
        List<String> destinationList = Arrays.asList("/");

        assertEquals(list,destinationList);
    }

    @Test
    public void testSetCurrentDir() throws FileSystemException {
        fs.setCurrentDir(testDir);
        assertEquals(testDir, fs.getCurrentDir());
    }

    @Test
    public void testSetCurrentDirNull() {
        assertThrows(NoSuchFileOrDirectoryException.class,
                () -> fs.setCurrentDir(null));
    }

    @Test
    public void testSetCurrentDirToRoot() throws FileSystemException {
        fs.setCurrentDir(root);
        assertEquals(root, fs.getCurrentDir());
    }

    @Test
    public void testFilePersistenceState() {
        assertFalse(fs.isSaved());
        assertNull(fs.getFileFullPathName());

        fs.setFileFullPathName("/path/to/filesystem.fs");

        assertTrue(fs.isSaved());
        assertEquals("/path/to/filesystem.fs", fs.getFileFullPathName());
    }

    @Test
    public void testIdGeneration() throws FileSystemException {
        long firstFileId = fs.createFile(testDir, "file1.txt").getId();
        long secondFileId = fs.createFile(testDir, "file2.txt").getId();
        long thirdFileId = fs.createFile(testDir, "file3.txt").getId();

        assertEquals(firstFileId + 1, secondFileId);
        assertEquals(secondFileId + 1, thirdFileId);
    }

    @Test
    public void testComplexHardlinkScenario() throws FileSystemException {
        FileINode originalFile = fs.createFile(testDir, "original.txt");
        DirectoryINode dir1 = fs.createDirectory(root, "dir1");
        DirectoryINode dir2 = fs.createDirectory(root, "dir2");

        fs.createHardlink(originalFile, dir1, "link1.txt");
        fs.createHardlink(originalFile, dir2, "link2.txt");

        assertEquals(3, originalFile.getReferenceCount());

        fs.removeINode(dir1, "link1.txt");
        assertEquals(2, originalFile.getReferenceCount());

        fs.removeINode(dir2, "link2.txt");
        assertEquals(1, originalFile.getReferenceCount());

        fs.removeINode(testDir, "original.txt");
        assertEquals(0, originalFile.getReferenceCount());
        assertFalse(testDir.getChildren().containsKey("original.txt"));
    }
}

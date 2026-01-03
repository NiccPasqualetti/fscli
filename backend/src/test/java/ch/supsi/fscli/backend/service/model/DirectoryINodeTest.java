package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.DirectoryNotEmptyException;
import ch.supsi.fscli.backend.exceptions.FileAlreadyExistsException;
import ch.supsi.fscli.backend.exceptions.InvalidFilenameException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryINodeTest {

    private DirectoryINode rootDir;
    private DirectoryINode dir1;
    private DirectoryINode dir2;

    @BeforeEach
    public void setUp() {
        rootDir = new DirectoryINode(1L,  null);

        dir1 = new DirectoryINode(2L,  rootDir);
        dir2 = new DirectoryINode(3L,  rootDir);
    }

    @Test
    public void testCreateDirectory() {
        assertNotNull(dir1);
        assertEquals(2L, dir1.getId());
        assertEquals(INodeType.DIRECTORY, dir1.getType());
        assertEquals(rootDir, dir1.getParent());
    }

    @Test
    public void testGetChildrenReturnsImmutableMap() throws FileAlreadyExistsException {
        DirectoryINode childDir = new DirectoryINode(4L,  dir1);
        FileINode childFile = new FileINode(5L,  dir1);

        dir1.addChild(childDir, "childDir");
        dir1.addChild(childFile, "childFile");

        Map<String, INode> children = dir1.getChildren();

        assertEquals(2, children.size());
        assertTrue(children.containsKey("childDir"));
        assertTrue(children.containsKey("childFile"));

        assertThrows(UnsupportedOperationException.class,
                () -> children.put("newChild", childDir));
        assertThrows(UnsupportedOperationException.class,
                () -> children.remove("childDir"));
    }

    @Test
    public void testAddChild() throws FileAlreadyExistsException {
        FileINode file = new FileINode(4L,  dir1);

        dir1.addChild(file, "testFile.txt");

        Map<String, INode> children = dir1.getChildren();
        assertEquals(1, children.size());
        assertEquals(file, children.get("testFile.txt"));
    }

    @Test
    public void testAddChildWithSameName() throws FileAlreadyExistsException {
        FileINode file1 = new FileINode(4L,  dir1);
        FileINode file2 = new FileINode(5L,  dir1);

        dir1.addChild(file1, "sameName.txt");
        assertThrows(FileAlreadyExistsException.class, ()-> dir1.addChild(file2, "sameName.txt"));
    }

    @Test
    public void testRemoveChildSuccessfully() throws NoSuchFileOrDirectoryException, DirectoryNotEmptyException, FileAlreadyExistsException {
        FileINode file = new FileINode(4L,  dir1);
        dir1.addChild(file, "fileToRemove.txt");

        assertEquals(1, dir1.getChildren().size());

        dir1.removeChild("fileToRemove.txt");

        assertEquals(0, dir1.getChildren().size());
    }

    @Test
    public void testRemoveChildThrowsNoSuchFileException() {
        assertThrows(NoSuchFileOrDirectoryException.class,
                () -> dir1.removeChild("nonExistentFile.txt"));
    }

    @Test
    public void testRemoveEmptyDirectorySuccessfully() throws Exception {
        DirectoryINode emptyDir = new DirectoryINode(4L,  dir1);
        dir1.addChild(emptyDir, "emptyDir");

        assertEquals(1, dir1.getChildren().size());

        dir1.removeChild("emptyDir");

        assertEquals(0, dir1.getChildren().size());
    }

    @Test
    public void testSearchInFolderFound() throws FileAlreadyExistsException {
        DirectoryINode searchDir = new DirectoryINode(4L,  dir1);
        dir1.addChild(searchDir, "aDir");
        FileINode file = new FileINode(4L, dir1);
        dir1.addChild(file, "aFile");
        SymlinkINode symlink = new SymlinkINode(4L, dir1, new FileINode(10L, dir1));
        dir1.addChild(symlink, "aSymlink");

        INode resultF = dir1.searchInFolder("aFile");
        INode resultD = dir1.searchInFolder("aDir");
        INode resultS = dir1.searchInFolder("aSymlink");

        assertEquals(file, resultF);
        assertEquals(searchDir, resultD);
        assertEquals(symlink, resultS);
    }

    @Test
    public void testSearchInFolderNotFound() {
        INode result = dir1.searchInFolder("nonExistent");
        assertNull(result);
    }

    @Test
    public void testEmptyDirectoryInitially() {
        assertTrue(dir1.getChildren().isEmpty());
        assertEquals(0, dir1.getChildren().size());
    }

    @Test
    public void testMultipleChildrenOperations() throws NoSuchFileOrDirectoryException, DirectoryNotEmptyException, FileAlreadyExistsException {
        FileINode file1 = new FileINode(4L,  dir1);
        FileINode file2 = new FileINode(5L,  dir1);
        DirectoryINode subDir = new DirectoryINode(6L,  dir1);
        SymlinkINode symlink = new SymlinkINode(7L, dir1, new FileINode(10L,dir1));

        dir1.addChild(file1, "file1.txt");
        dir1.addChild(file2, "file2.txt");
        dir1.addChild(subDir, "subdirectory");
        dir1.addChild(symlink, "link");

        assertEquals(4, dir1.getChildren().size());

        dir1.removeChild("file1.txt");
        assertEquals(3, dir1.getChildren().size());
        assertFalse(dir1.getChildren().containsKey("file1.txt"));

        assertTrue(dir1.getChildren().containsKey("file2.txt"));
        assertTrue(dir1.getChildren().containsKey("subdirectory"));
        assertTrue(dir1.getChildren().containsKey("link"));
    }

    @Test
    public void testRootDirectoryHasNullParent() {
        assertNull(rootDir.getParent());
    }

    @Test
    public void testNonRootDirectoryHasParent() {
        assertEquals(rootDir, dir1.getParent());
        assertEquals(rootDir, dir2.getParent());
    }
}

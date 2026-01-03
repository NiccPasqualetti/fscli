package ch.supsi.fscli.backend.service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymlinkINodeTest {

    private DirectoryINode parentDir;
    private FileINode targetFile;
    private DirectoryINode targetDir;
    private SymlinkINode symlinkToFile;
    private SymlinkINode symlinkToDir;

    @BeforeEach
    public void setUp() {
        parentDir = new DirectoryINode(1L, null);
        targetFile = new FileINode(2L, parentDir);
        targetDir = new DirectoryINode(3L, parentDir);

        symlinkToFile = new SymlinkINode(4L, parentDir, targetFile);
        symlinkToDir = new SymlinkINode(5L, parentDir, targetDir);
    }

    @Test
    public void testCreateSymlinkINode() {
        assertNotNull(symlinkToFile);
        assertEquals(4L, symlinkToFile.getId());
        assertEquals(INodeType.SYM_LINK, symlinkToFile.getType());
        assertEquals(parentDir, symlinkToFile.getParent());
    }

    @Test
    public void testGetDestination() {
        assertEquals(targetFile, symlinkToFile.getDestination());
        assertEquals(targetDir, symlinkToDir.getDestination());
    }

    @Test
    public void testInitialReferenceCount() {
        assertEquals(1, symlinkToFile.getReferenceCount());
        assertEquals(1, symlinkToDir.getReferenceCount());
    }

    @Test
    public void testIncreaseReferenceCount() {
        symlinkToFile.increaseReferenceCount();
        assertEquals(2, symlinkToFile.getReferenceCount());

        symlinkToFile.increaseReferenceCount();
        assertEquals(3, symlinkToFile.getReferenceCount());
    }

    @Test
    public void testDecreaseReferenceCount() {
        symlinkToFile.decreaseReferenceCount();
        assertEquals(0, symlinkToFile.getReferenceCount());
    }

    @Test
    public void testSymlinkToDifferentTargetTypes() {
        assertEquals(INodeType.FILE, symlinkToFile.getDestination().getType());
        assertEquals(targetFile, symlinkToFile.getDestination());

        assertEquals(INodeType.DIRECTORY, symlinkToDir.getDestination().getType());
        assertEquals(targetDir, symlinkToDir.getDestination());
    }

    @Test
    public void testSymlinkIndependenceFromTarget() {
        long initialTargetRefCount = targetFile.getReferenceCount();

        symlinkToFile.increaseReferenceCount();
        symlinkToFile.increaseReferenceCount();

        assertEquals(initialTargetRefCount, targetFile.getReferenceCount());
        assertEquals(3, symlinkToFile.getReferenceCount());
    }

    @Test
    public void testSymlinkWithNullDestination() {
        SymlinkINode symlinkWithNull = new SymlinkINode(6L, parentDir, null);
        assertNull(symlinkWithNull.getDestination());
    }
}

package ch.supsi.fscli.backend.service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileINodeTest {

    private DirectoryINode parentDir;
    private FileINode fileINode;

    @BeforeEach
    public void setUp() {
        parentDir = new DirectoryINode(1L, null);
        fileINode = new FileINode(2L, parentDir);
    }

    @Test
    public void testCreateFileINode() {
        assertNotNull(fileINode);
        assertEquals(2L, fileINode.getId());
        assertEquals(INodeType.FILE, fileINode.getType());
        assertEquals(parentDir, fileINode.getParent());
    }

    @Test
    public void testIncreaseReferenceCount() {
        fileINode.increaseReferenceCount();
        assertEquals(2, fileINode.getReferenceCount());

        fileINode.increaseReferenceCount();
        assertEquals(3, fileINode.getReferenceCount());
    }

    @Test
    public void testDecreaseReferenceCount() {
        fileINode.decreaseReferenceCount();
        assertEquals(0, fileINode.getReferenceCount());
    }

    @Test
    public void testReferenceCountAfterMultipleHardLinks() {
        for (int i = 0; i < 5; i++) {
            fileINode.increaseReferenceCount();
        }
        assertEquals(6, fileINode.getReferenceCount());

        for (int i = 0; i < 3; i++) {
            fileINode.decreaseReferenceCount();
        }
        assertEquals(3, fileINode.getReferenceCount());
    }
}

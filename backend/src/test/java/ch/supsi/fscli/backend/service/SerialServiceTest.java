package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.exceptions.FileAlreadyExistsException;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.INode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SerialServiceTest {

    private SerialService serialService;
    private CreateFilesystemService createFilesystemServiceMock;
    private Filesystem testFilesystem;

    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() {
        createFilesystemServiceMock = mock(CreateFilesystemService.class);
        serialService = new SerialService(createFilesystemServiceMock);
        testFilesystem = new Filesystem("TestFS");
        tempFile = tempDir.resolve("test_fs.json").toFile();
    }

    @AfterEach
    void tearDown() {
        reset(createFilesystemServiceMock);
    }

    @Test
    void testSave() throws IOException {
        testFilesystem.setFileFullPathName(tempFile.getAbsolutePath());
        when(createFilesystemServiceMock.getFilesystem()).thenReturn(testFilesystem);

        serialService.save();

        assertTrue(tempFile.exists(), "Il file di salvataggio deve esistere");
        assertTrue(tempFile.length() > 0, "Il file non deve essere vuoto");
        verify(createFilesystemServiceMock, times(1)).getFilesystem();
    }

    @Test
    void testSaveAs() throws IOException {
        when(createFilesystemServiceMock.getFilesystem()).thenReturn(testFilesystem);

        serialService.saveAs(tempFile);

        assertTrue(tempFile.exists());
        assertEquals(tempFile.getAbsolutePath(), testFilesystem.getFileFullPathName());
    }

    @Test
    void testOpenAndRebuildStructure() throws IOException, FileAlreadyExistsException {
        DirectoryINode root = testFilesystem.getRoot();
        DirectoryINode subDir = new DirectoryINode(2L, root);
        root.addChild(subDir, "subdir");

        DirectoryINode nestedDir = new DirectoryINode(3L, subDir);
        subDir.addChild(nestedDir, "nested");

        assertEquals(root, subDir.getParent());
        assertEquals(subDir, nestedDir.getParent());

        testFilesystem.setFileFullPathName(tempFile.getAbsolutePath());
        when(createFilesystemServiceMock.getFilesystem()).thenReturn(testFilesystem);
        serialService.save();

        serialService.open(tempFile);

        ArgumentCaptor<Filesystem> fsCaptor = ArgumentCaptor.forClass(Filesystem.class);
        verify(createFilesystemServiceMock).setFilesystem(fsCaptor.capture());

        Filesystem loadedFs = fsCaptor.getValue();
        assertNotNull(loadedFs);

        DirectoryINode loadedRoot = loadedFs.getRoot();

        INode loadedSubDirNode = loadedRoot.getChildren().get("subdir");
        assertNotNull(loadedSubDirNode, "La sottocartella deve esistere");
        assertInstanceOf(DirectoryINode.class, loadedSubDirNode);
        DirectoryINode loadedSubDir = (DirectoryINode) loadedSubDirNode;

        assertNotNull(loadedSubDir.getParent(), "Il parent non dovrebbe essere null dopo rebuildParentStructure");
        assertEquals(loadedRoot, loadedSubDir.getParent(), "Il parent della subdir deve essere la root");

        INode loadedNestedNode = loadedSubDir.getChildren().get("nested");
        assertNotNull(loadedNestedNode);

        assertEquals(loadedSubDir, loadedNestedNode.getParent(), "Il parent della nested deve essere subdir");
    }

    @Test
    void testIsAlreadySaved() {
        Filesystem fsSaved = mock(Filesystem.class);
        when(fsSaved.isSaved()).thenReturn(true);
        when(createFilesystemServiceMock.getFilesystem()).thenReturn(fsSaved);

        assertTrue(serialService.isAlreadySaved());
    }

    @Test
    void testOpenThrowsIOExceptionOnMissingFile() {
        File nonExistent = new File(tempDir.toFile(), "ghost.json");
        assertThrows(IOException.class, () -> serialService.open(nonExistent));
    }
}

package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RmServiceTest {

    private RmService rmService;
    private DirectoryINode parent;

    @BeforeEach
    public void setUp() throws FileSystemException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        Filesystem filesystem = createFilesystemService.createFilesystem("Whatever");
        rmService = new RmService("", createFilesystemService);

        parent = filesystem.createDirectory(filesystem.getRoot(), "dir1");
        filesystem.createFile(parent, "file1.txt");
    }

    @Test
    public void removeDeletesExistingFile() throws FileSystemException {
        assertTrue(parent.getChildren().containsKey("file1.txt"));
        rmService.execute(List.of("/dir1/file1.txt"), List.of());
        assertFalse(parent.getChildren().containsKey("file1.txt"));
    }

    @Test
    public void removeNonExistingFile_isIgnored() {
        int before = parent.getChildren().size();
        assertThrows(
                NoSuchFileOrDirectoryException.class,
                ()-> rmService.execute(List.of("/dir1/ghost.txt"), List.of())
        );
        int after = parent.getChildren().size();
        assertEquals(before, after);
    }

}

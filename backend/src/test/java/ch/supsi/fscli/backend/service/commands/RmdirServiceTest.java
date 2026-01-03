package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.DirectoryNotEmptyException;
import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RmdirServiceTest {

    private Filesystem filesystem;
    private RmdirService rmdirService;
    private DirectoryINode parent;

    @BeforeEach
    public void setUp() throws FileSystemException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("Whatever");
        rmdirService = new RmdirService("",createFilesystemService);

        parent = filesystem.createDirectory(filesystem.getRoot(), "dir1");
        filesystem.createDirectory(parent, "dir2");
    }

    @Test
    public void rmdirRemovesEmptyDirectory() throws FileSystemException {
        assertTrue(parent.getChildren().containsKey("dir2"));
        rmdirService.execute(List.of("/dir1/dir2"), List.of());
        assertFalse(parent.getChildren().containsKey("dir2"));
    }

    @Test
    public void rmdirNonExistingDirectoryHandledGracefully() {
        assertThrows(NoSuchFileOrDirectoryException.class, () -> rmdirService.execute(List.of("/dir1/ghostDir"), List.of()));
    }

    @Test
    public void rmdirFailsOnNonEmptyDirectory() throws FileSystemException {
        DirectoryINode nested = filesystem.createDirectory(parent, "nonEmpty");
        filesystem.createDirectory(nested, "child");

        assertThrows(
                DirectoryNotEmptyException.class,
                ()->rmdirService.execute(List.of("/dir1/nonEmpty"), List.of())
        );
    }
}

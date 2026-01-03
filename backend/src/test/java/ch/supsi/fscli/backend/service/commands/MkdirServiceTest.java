package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MkdirServiceTest {

    private Filesystem filesystem;
    private MkdirService mkdirService;

    @BeforeEach
    public void setUp() {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("Whatever");
        mkdirService = new MkdirService("",createFilesystemService);
    }

    @Test
    public void mkdirCreatesDirectoryInRoot() throws FileSystemException {
        mkdirService.execute(List.of("/dir1"), List.of());
        DirectoryINode root = filesystem.getRoot();

        Map<String, ?> children = root.getChildren();
        assertTrue(children.containsKey("dir1"), "Root should contain dir1");
    }

    @Test
    public void mkdirCreatesNestedDirectory() throws FileSystemException {
        mkdirService.execute(List.of("/dir1"), List.of());
        mkdirService.execute(List.of("/dir1/dir2"), List.of());

        DirectoryINode dir1 = (DirectoryINode) filesystem.getRoot().getChildren().get("dir1");
        assertTrue(dir1.getChildren().containsKey("dir2"), "dir1 should contain dir2");
    }

    @Test
    public void mkdirThowDirecoryAlreadyExists() {
        assertDoesNotThrow(() -> mkdirService.execute(List.of("/dir1"), List.of()));
        assertThrows(FileSystemException.class, () -> mkdirService.execute(List.of("/dir1"), List.of()));
    }
}

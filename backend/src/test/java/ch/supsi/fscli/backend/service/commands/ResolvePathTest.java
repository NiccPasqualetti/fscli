package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ResolvePathTest {

    private Filesystem filesystem;
    private List<String> pathAsList_exist;
    private List<String> pathAsList_no_exist;
    private List<String> pathAsList_root;

    @BeforeEach
    public void setUp() throws FileSystemException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("Whatever");
        MkdirService mkdirService = new MkdirService("",createFilesystemService);

        mkdirService.execute(List.of("/directory1/"), List.of());
        mkdirService.execute(List.of("/directory1/directory2"), List.of());
        mkdirService.execute(List.of("/directory1/directory2/directory3"), List.of());

        pathAsList_exist = Arrays.asList("/","directory1", "directory2", "directory3");
        pathAsList_no_exist = Arrays.asList("/","directory1", "directory3", "directory2");
        pathAsList_root = List.of("/");
    }


    @Test
    public void testResolvePath() throws NoSuchFileOrDirectoryException {
        assertNotNull(filesystem.resolvePath(pathAsList_exist));
        assertThrows(NoSuchFileOrDirectoryException.class, () -> filesystem.resolvePath(pathAsList_no_exist));
        assertEquals(filesystem.resolvePath(pathAsList_root),filesystem.getRoot());
    }
}

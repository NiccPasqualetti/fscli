package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.controller.CommandController;
import ch.supsi.fscli.backend.exceptions.CommandUnknownException;
import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for wildcard expansion in the command controller.
 *
 * These tests verify that:
 *  - patterns like "*.txt" are expanded to all matching files in the current directory
 *  - patterns like "dir/*.txt" work for subdirectories
 *  - multi-level patterns paths are expanded correctly
 *  - patterns that match nothing keep the filesystem unchanged and trigger the same
 *    error behavior as non-existing paths.
 */
public class WildcardTest {

    private CreateFilesystemService createFilesystemService;
    private Filesystem filesystem;
    private TouchService touchService;
    private RmService rmService;
    private CommandController commandController;

    @BeforeEach
    public void setup() {
        createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("test");

        touchService = new TouchService("description.touch", createFilesystemService);
        rmService = new RmService("description.rm", createFilesystemService);

        List<Command> commands = new ArrayList<>();
        commands.add(rmService);
        commandController = new CommandController(commands, createFilesystemService);
    }

    @Test
    public void rmWildcardRemovesAllMatchingFilesInCurrentDir() {
        try {
            touchService.execute(List.of("a.txt", "b.txt", "c.log"), List.of());

            DirectoryINode root = filesystem.getRoot();
            assertTrue(root.getChildren().containsKey("a.txt"));
            assertTrue(root.getChildren().containsKey("b.txt"));
            assertTrue(root.getChildren().containsKey("c.log"));

            commandController.parse("rm", new String[]{"*.txt"});

            assertFalse(root.getChildren().containsKey("a.txt"));
            assertFalse(root.getChildren().containsKey("b.txt"));
            assertTrue(root.getChildren().containsKey("c.log"));
        } catch (FileSystemException | CommandUnknownException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void rmWildcardRemovesFilesInSubdirectory() {
        try {
            DirectoryINode dir = filesystem.createDirectory(filesystem.getRoot(), "dir");
            touchService.execute(
                    List.of("dir/a.txt", "dir/b.txt", "dir/c.log"),
                    List.of()
            );

            assertTrue(dir.getChildren().containsKey("a.txt"));
            assertTrue(dir.getChildren().containsKey("b.txt"));
            assertTrue(dir.getChildren().containsKey("c.log"));

            commandController.parse("rm", new String[]{"dir/*.txt"});

            assertFalse(dir.getChildren().containsKey("a.txt"));
            assertFalse(dir.getChildren().containsKey("b.txt"));
            assertTrue(dir.getChildren().containsKey("c.log"));
        } catch (FileSystemException | CommandUnknownException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void rmWildcardWithNoMatchesDoesNotChangeFilesystem() {
        try {
            touchService.execute(List.of("only.txt"), List.of());
            DirectoryINode root = filesystem.getRoot();
            int before = root.getChildren().size();

            assertThrows(
                    NoSuchFileOrDirectoryException.class,
                    () -> commandController.parse("rm", new String[]{"*.log"})
            );

            int after = root.getChildren().size();
            assertEquals(before, after);
            assertTrue(root.getChildren().containsKey("only.txt"));
        } catch (FileSystemException e) {
            fail("Unexpected FileSystemException: " + e.getMessage());
        } catch (CommandUnknownException e) {
            fail("Command 'rm' should be known: " + e.getMessage());
        }
    }

    @Test
    public void rmMultiLevelWildcardAbsolutePath() {
        try {
            DirectoryINode root = filesystem.getRoot();
            DirectoryINode dir1 = filesystem.createDirectory(root, "dir1");
            DirectoryINode a = filesystem.createDirectory(dir1, "a");
            DirectoryINode b = filesystem.createDirectory(dir1, "b");

            touchService.execute(
                    List.of(
                            "/dir1/a/x.txt",
                            "/dir1/a/y.log",
                            "/dir1/b/z.txt",
                            "/dir1/b/n.log",
                            "/dir1/c.txt"
                    ),
                    List.of()
            );

            assertTrue(a.getChildren().containsKey("x.txt"));
            assertTrue(a.getChildren().containsKey("y.log"));
            assertTrue(b.getChildren().containsKey("z.txt"));
            assertTrue(b.getChildren().containsKey("n.log"));
            assertTrue(dir1.getChildren().containsKey("c.txt"));

            commandController.parse("rm", new String[]{"/dir1/*/*.txt"});

            assertFalse(a.getChildren().containsKey("x.txt"));
            assertTrue(a.getChildren().containsKey("y.log"));

            assertFalse(b.getChildren().containsKey("z.txt"));
            assertTrue(b.getChildren().containsKey("n.log"));

            assertTrue(dir1.getChildren().containsKey("c.txt"));
        } catch (FileSystemException | CommandUnknownException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void rmMultiLevelWildcardRelativePath() {
        try {
            DirectoryINode root = filesystem.getRoot();
            DirectoryINode dir1 = filesystem.createDirectory(root, "dir1");
            DirectoryINode a = filesystem.createDirectory(dir1, "a");
            DirectoryINode b = filesystem.createDirectory(dir1, "b");

            touchService.execute(
                    List.of(
                            "dir1/a/x.txt",
                            "dir1/a/y.log",
                            "dir1/b/z.txt",
                            "dir1/b/n.log"
                    ),
                    List.of()
            );

            assertTrue(a.getChildren().containsKey("x.txt"));
            assertTrue(a.getChildren().containsKey("y.log"));
            assertTrue(b.getChildren().containsKey("z.txt"));
            assertTrue(b.getChildren().containsKey("n.log"));

            commandController.parse("rm", new String[]{"dir1/*/*.txt"});

            assertFalse(a.getChildren().containsKey("x.txt"));
            assertTrue(a.getChildren().containsKey("y.log"));

            assertFalse(b.getChildren().containsKey("z.txt"));
            assertTrue(b.getChildren().containsKey("n.log"));
        } catch (FileSystemException | CommandUnknownException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}

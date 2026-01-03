package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileAlreadyExistsException;
import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.InvalidFilenameException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.FileINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.INode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TouchServiceTest {

    private Filesystem filesystem;
    private TouchService touchService;

    private String operand;
    private String name;

    @BeforeEach
    public void setUp() throws FileAlreadyExistsException, InvalidFilenameException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        filesystem = createFilesystemService.createFilesystem("Whatever");
        touchService = new TouchService("",createFilesystemService);

        DirectoryINode d1 = filesystem.createDirectory(filesystem.getRoot(), "Directory1");
        DirectoryINode d2 = filesystem.createDirectory(d1, "Directory2");
        filesystem.createDirectory(d2, "Directory3");
        name = "file_name";
        operand = "/Directory1/Directory2/Directory3/"+name;
    }

    @Test
    public void touchCreatesFileInTargetDirectory() throws FileSystemException {
        touchService.execute(List.of(operand), List.of());

        DirectoryINode parent = filesystem.resolvePath(operand).getParent();

        Map<String, INode> children = parent.getChildren();
        assertTrue(children.containsKey(name));

        assertInstanceOf(FileINode.class, children.get(name));
    }

    @Test
    public void touchCreateMultipleFiles() throws FileSystemException {
        String file2name = "test.txt";
        touchService.execute(List.of(operand, file2name), List.of());
        DirectoryINode parent = filesystem.resolvePath(operand).getParent();

        Map<String, INode> children = parent.getChildren();
        assertTrue(children.containsKey(name));
        assertInstanceOf(FileINode.class, children.get(name));

        children = filesystem.getRoot().getChildren();
        assertTrue(children.containsKey(file2name));
        assertInstanceOf(FileINode.class, children.get(file2name));
    }

    @Test
    public void touchCreateTwoIdenticalFiles() {
        assertThrows(FileAlreadyExistsException.class, () -> touchService.execute(List.of("a", "a"), List.of()));
    }
}

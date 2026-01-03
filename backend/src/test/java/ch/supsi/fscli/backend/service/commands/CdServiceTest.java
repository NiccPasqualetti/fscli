package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.SymlinkINode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CdServiceTest {
    private Filesystem fs;
    private DirectoryINode root;
    private CdService cdService;
    private DirectoryINode documents;

    @BeforeEach
    public void setUp() throws FileAlreadyExistsException, InvalidFilenameException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        fs = createFilesystemService.createFilesystem("TestFS");
        cdService = new CdService("",createFilesystemService);

        root = fs.getRoot();

        var home = fs.createDirectory(root, "home");
        var user = fs.createDirectory(home, "user");
        documents = fs.createDirectory(user, "Documents");
        var doc = fs.createFile(documents, "doc.txt");
        var pictures = fs.createDirectory(user, "Pictures");
        var album1 = fs.createDirectory(pictures, "album1");
        var img1 = fs.createFile(album1, "img1.jxl");

        SymlinkINode linkToDocuments = fs.createSymlink(documents, album1, "linkToDocuments");
    }

    @Test
    public void changeDirAbsolute() throws FileSystemException {
        fs.setCurrentDir(root);
        List<String> operands = new ArrayList<>();
        operands.add("/home/user/Documents");
        cdService.execute(operands, List.of());
        assertEquals(fs.getCurrentDir().getId(), fs.resolvePath("/home/user/Documents").getId());
    }

    @Test
    public void changeDirRelative() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user/Documents"));
        List<String> operands = new ArrayList<>();
        operands.add("../Pictures");
        cdService.execute(operands, List.of());
        assertEquals(fs.getCurrentDir().getId(), fs.resolvePath("/home/user/Pictures").getId());
        cdService.execute(List.of(), List.of());
    }

    @Test
    public void changeDirSymlink() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user/Documents"));
        List<String> operands = new ArrayList<>();
        operands.add("/home/user/Pictures/album1/linkToDocuments");
        cdService.execute(operands, List.of());
        assertEquals(fs.getCurrentDir().getId(), documents.getId());
    }

    @Test
    public void changeDirMultipleParentsToRoot() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user/Documents"));
        List<String> operands = new ArrayList<>();
        operands.add("../../..");
        cdService.execute(operands, List.of());
        assertEquals(root.getId(), fs.getCurrentDir().getId());
    }
}

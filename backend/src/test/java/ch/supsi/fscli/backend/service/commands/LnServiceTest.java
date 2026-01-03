package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.SymlinkINode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LnServiceTest {

    private Filesystem fs;
    private DirectoryINode root;
    private LnService lnService;
    private DirectoryINode documents;

    @BeforeEach
    public void setUp() throws FileAlreadyExistsException, InvalidFilenameException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        fs = createFilesystemService.createFilesystem("TestFS");
        lnService = new LnService("", createFilesystemService);

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
    public void testOperandsSize() throws FileSystemException {
        assertThrows(TooManyArgumentsException.class,
                () -> lnService.execute(List.of("a","b","c"), List.of()));
        assertThrows(MissingOperandsException.class,
                () -> lnService.execute(List.of("a"), List.of()));
        assertThrows(NoSuchFileOrDirectoryException.class, () ->
                lnService.execute(List.of("a","b"), List.of()));
        assertThrows(TooManyArgumentsException.class,
                () -> lnService.execute(List.of("a","b"), List.of("f1", "f2")));
    }

    /**
     * Create a symlink that points to a directory
     */
    @Test
    public void testCreateSymlinkDir() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home"));
        List<String> operands = List.of("/home/user/Pictures", "linkToPictures");
        List<String> flags = List.of("s");
        lnService.execute(operands,flags);

        assertEquals(fs.resolvePath("/home/user/Pictures").getId(), ((SymlinkINode)fs.resolvePath("/home/linkToPictures")).getDestination().getId());
    }

    /**
     * Create a symlink that points to a file
     */
    @Test
    public void testCreateSymlinkFile() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));
        List<String> operands = List.of("Documents/doc.txt", "linkToDoc");
        List<String> flags = List.of("s");
        lnService.execute(operands, flags);

        assertEquals(fs.resolvePath("/home/user/Documents/doc.txt").getId(),
                ((SymlinkINode)fs.resolvePath("/home/user/linkToDoc")).getDestination().getId());
    }

    /**
     * Create a symlink that points to a symlink's original destination
     */
    @Test
    public void testCreateSymlinkSymlink() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));
        List<String> operands1 = List.of("Pictures", "picsLink");
        List<String> flags = List.of("s");
        lnService.execute(operands1, flags);

        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));
        List<String> operands2 = List.of("picsLink", "secondPicsLink");
        lnService.execute(operands2, flags);

        SymlinkINode firstSymlink = (SymlinkINode) fs.resolvePath("/home/user/picsLink");
        long originalDestinationId = firstSymlink.getDestination().getId();

        SymlinkINode secondSymlink = (SymlinkINode) fs.resolvePath("/home/user/secondPicsLink");
        long secondSymlinkDestinationId = secondSymlink.getDestination().getId();

        assertEquals(originalDestinationId, secondSymlinkDestinationId);
    }

    /**
     * Create a hardlink that points to a file
     */
    @Test
    public void testCreateHardlinkFile() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user/Pictures/album1"));
        List<String> operands = List.of("img1.jxl", "hardlinkToImg");
        List<String> flags = List.of();
        lnService.execute(operands, flags);

        long originalId = fs.resolvePath("/home/user/Pictures/album1/img1.jxl").getId();
        long hardlinkId = fs.resolvePath("/home/user/Pictures/album1/hardlinkToImg").getId();

        assertEquals(originalId, hardlinkId);

    }

    /**
     * Create a hardlink to a symlink, the hardlink will point to the original file (no directories)
     */
    @Test
    public void testCreateHardlinkSymlink() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));
        List<String> operands1 = List.of("Documents", "docsLink");
        List<String> flags1 = List.of("s");
        lnService.execute(operands1, flags1);

        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));
        List<String> operands2 = List.of("docsLink", "hardlinkToDocsLink");
        List<String> flags2 = List.of();
        lnService.execute(operands2, flags2);

        long symlinkId = fs.resolvePath("/home/user/docsLink").getId();
        long hardlinkId = fs.resolvePath("/home/user/hardlinkToDocsLink").getId();

        assertEquals(symlinkId, hardlinkId);

        assertEquals(ch.supsi.fscli.backend.service.model.INodeType.SYM_LINK,
                fs.resolvePath("/home/user/docsLink").getType());
        assertEquals(ch.supsi.fscli.backend.service.model.INodeType.SYM_LINK,
                fs.resolvePath("/home/user/hardlinkToDocsLink").getType());

        SymlinkINode originalSymlink = (SymlinkINode) fs.resolvePath("/home/user/docsLink");
        SymlinkINode hardlinkSymlink = (SymlinkINode) fs.resolvePath("/home/user/hardlinkToDocsLink");
        assertEquals(originalSymlink.getDestination().getId(),
                hardlinkSymlink.getDestination().getId());
    }
}

package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MvServiceTest {
    private Filesystem fs;
    private DirectoryINode root;
    private MvService mvService;
    private DirectoryINode documents;
    private DirectoryINode pictures;
    private DirectoryINode album1;
    private DirectoryINode home;
    private DirectoryINode user;
    private FileINode docFile;
    private FileINode imgFile;

    @BeforeEach
    public void setUp() throws FileAlreadyExistsException, InvalidFilenameException {
        CreateFilesystemService createFilesystemService = new CreateFilesystemService();
        fs = createFilesystemService.createFilesystem("TestFS");
        mvService = new MvService("", createFilesystemService);

        root = fs.getRoot();

        home = fs.createDirectory(root, "home");
        user = fs.createDirectory(home, "user");
        documents = fs.createDirectory(user, "Documents");
        docFile = fs.createFile(documents, "doc.txt");
        pictures = fs.createDirectory(user, "Pictures");
        album1 = fs.createDirectory(pictures, "album1");
        imgFile = fs.createFile(album1, "img1.jxl");

        fs.createFile(documents, "report.pdf");
        fs.createFile(documents, "notes.txt");
        fs.createDirectory(documents, "subfolder");
        fs.createFile(album1, "photo.jpg");
    }

    /**
     * Test moving a file to a directory using relative path
     */
    @Test
    public void testMoveRel() throws FileSystemException {
        fs.setCurrentDir((DirectoryINode) fs.resolvePath("/home/user"));

        List<String> operands = List.of("Documents/doc.txt", "Pictures");
        mvService.execute(operands, List.of());

        assertNull(documents.searchInFolder("doc.txt"));
        assertNotNull(pictures.searchInFolder("doc.txt"));

        INode movedFile = pictures.searchInFolder("doc.txt");
        assertEquals(docFile.getId(), movedFile.getId());
    }

    /**
     * Test moving a file to a directory using absolute path
     */
    @Test
    public void testMoveAbs() throws FileSystemException {
        List<String> operands = List.of("/home/user/Pictures/album1/img1.jxl", "/home/user/Documents");
        mvService.execute(operands, List.of());

        assertNull(album1.searchInFolder("img1.jxl"));
        assertNotNull(documents.searchInFolder("img1.jxl"));

        INode movedFile = documents.searchInFolder("img1.jxl");
        assertEquals(imgFile.getId(), movedFile.getId());
    }

    /**
     * Test moving multiple files/dirs to a directory
     */
    @Test
    public void testMoveMulti() throws FileSystemException {
        FileINode notesFile = (FileINode) documents.searchInFolder("notes.txt");
        FileINode reportFile = (FileINode) documents.searchInFolder("report.pdf");
        FileINode photoFile = (FileINode) album1.searchInFolder("photo.jpg");

        List<String> operands = List.of(
                "/home/user/Documents/notes.txt",
                "/home/user/Documents/report.pdf",
                "/home/user/Pictures/album1/photo.jpg",
                "/home/user/Pictures"
        );
        mvService.execute(operands, List.of());

        assertNull(documents.searchInFolder("notes.txt"));
        assertNull(documents.searchInFolder("report.pdf"));
        assertNull(album1.searchInFolder("photo.jpg"));

        assertNotNull(pictures.searchInFolder("notes.txt"));
        assertNotNull(pictures.searchInFolder("report.pdf"));
        assertNotNull(pictures.searchInFolder("photo.jpg"));

        assertEquals(notesFile.getId(), pictures.searchInFolder("notes.txt").getId());
        assertEquals(reportFile.getId(), pictures.searchInFolder("report.pdf").getId());
        assertEquals(photoFile.getId(), pictures.searchInFolder("photo.jpg").getId());
    }

    /**
     * Test rename a file and a directory (in the same test)
     */
    @Test
    public void testRename() throws FileSystemException {
        List<String> operands = List.of("/home/user/Documents/doc.txt", "/home/user/Documents/document.txt");
        mvService.execute(operands, List.of());

        assertNull(documents.searchInFolder("doc.txt"));
        assertNotNull(documents.searchInFolder("document.txt"));
        assertEquals(docFile.getId(), documents.searchInFolder("document.txt").getId());

        operands = List.of("/home/user/Pictures/album1", "/home/user/Pictures/photos");
        mvService.execute(operands, List.of());

        assertNull(pictures.searchInFolder("album1"));
        assertNotNull(pictures.searchInFolder("photos"));

        DirectoryINode renamedDir = (DirectoryINode) pictures.searchInFolder("photos");
        assertNotNull(renamedDir.searchInFolder("img1.jxl"));
    }

    /**
     * Test move a directory into itself (this must fail)
     */
    @Test
    public void testMoveDirectoryIntoItself() throws FileAlreadyExistsException, InvalidFilenameException {
        DirectoryINode subfolder = (DirectoryINode) documents.searchInFolder("subfolder");
        DirectoryINode inner = fs.createDirectory(subfolder, "inner");
        fs.createFile(inner, "test.txt");

        List<String> operands = List.of("/home/user/Documents/subfolder", "/home/user/Documents/subfolder/inner");

        assertThrows(InvalidDirectoryException.class, () -> mvService.execute(operands, List.of()));

        assertNotNull(documents.searchInFolder("subfolder"));
        assertNotNull(subfolder.searchInFolder("inner"));
    }

    /**
     * Test moving when source doesn't exist
     */
    @Test
    public void testMoveNonExistentSource() {
        List<String> operands = List.of("/home/user/nonexistent.txt", "/home/user/Documents");

        assertThrows(NoSuchFileOrDirectoryException.class, () -> mvService.execute(operands, List.of()));
    }

    /**
     * Test moving to non-existent parent directory
     */
    @Test
    public void testMoveToNonExistentParent() {
        List<String> operands = List.of("/home/user/Documents/doc.txt", "/home/user/Nonexistent/newfile.txt");

        assertThrows(NoSuchFileOrDirectoryException.class, () -> mvService.execute(operands, List.of()));
    }

    /**
     * Test moving with too few operands
     */
    @Test
    public void testTooFewOperands() {
        List<String> operands = List.of("onlyOne");

        assertThrows(MissingOperandsException.class, () -> mvService.execute(operands, List.of()));
    }

    /**
     * Test moving with flags (should fail)
     */
    @Test
    public void testWithFlagsShouldFail() {
        List<String> operands = List.of("/home/user/Documents/doc.txt", "/home/user/Pictures");
        List<String> flags = List.of("-f");

        assertThrows(TooManyArgumentsException.class, () -> mvService.execute(operands, flags));
    }

    /**
     * Test moving file to existing file name (should fail on rename)
     */
    @Test
    public void testMoveToExistingFileName() throws FileSystemException {
        fs.createFile(pictures, "existing.txt");

        List<String> operands = List.of("/home/user/Documents/doc.txt", "/home/user/Pictures/existing.txt");

        assertThrows(FileAlreadyExistsException.class, () -> mvService.execute(operands, List.of()));
    }

    /**
     * Test moving directory with contents to another location
     */
    @Test
    public void testMoveDirectoryWithContents() throws FileSystemException {
        fs.createFile(documents, "file1.txt");
        fs.createFile(documents, "file2.txt");
        DirectoryINode docsSubfolder = (DirectoryINode) documents.searchInFolder("subfolder");
        fs.createFile(docsSubfolder, "nested.txt");

        List<String> operands = List.of("/home/user/Documents", "/home/user/Pictures");
        mvService.execute(operands, List.of());

        assertNull(user.searchInFolder("Documents"));
        DirectoryINode movedDocuments = (DirectoryINode) pictures.searchInFolder("Documents");
        assertNotNull(movedDocuments);

        assertNotNull(movedDocuments.searchInFolder("doc.txt"));
        assertNotNull(movedDocuments.searchInFolder("file1.txt"));
        assertNotNull(movedDocuments.searchInFolder("file2.txt"));
        assertNotNull(movedDocuments.searchInFolder("subfolder"));

        DirectoryINode movedSubfolder = (DirectoryINode) movedDocuments.searchInFolder("subfolder");
        assertNotNull(movedSubfolder.searchInFolder("nested.txt"));
    }

    /**
     * Move content where the destination is a link that points to a directory
     */
    @Test
    public void testMoveViaSymlink() throws FileSystemException {
        fs.createSymlink(pictures, documents, "linkToPictures");

        List<String> operands = List.of("/home/user/Documents/doc.txt", "/home/user/Documents/linkToPictures");
        mvService.execute(operands, List.of());

        assertNull(documents.searchInFolder("doc.txt"));
        assertNotNull(pictures.searchInFolder("doc.txt"));

        INode movedFile = pictures.searchInFolder("doc.txt");
        assertEquals(docFile.getId(), movedFile.getId());
    }
}

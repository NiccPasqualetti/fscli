package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.TooManyArgumentsException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PwdServiceTest {
    private List<String> operands, flags;

    private Filesystem fs;
    private PwdService pwdService;

    private DirectoryINode ciaoDir;
    private DirectoryINode testDir;
    private DirectoryINode cartellaDir;
    private DirectoryINode lolDir;

    @BeforeEach
    public void setup(){
        operands = flags = new ArrayList<>();
        CreateFilesystemService  createFilesystemService = new CreateFilesystemService();
        fs = createFilesystemService.createFilesystem("test");
        pwdService = new PwdService("",createFilesystemService);
        try{

            ciaoDir = fs.createDirectory(fs.getRoot(), "ciao");
            testDir = fs.createDirectory(fs.getRoot(), "test");
            cartellaDir = fs.createDirectory(testDir, "cartella");
            lolDir = fs.createDirectory(cartellaDir, "lol");

        }catch (FileSystemException e){
            fail("Error while creating the behaviour of filesystem");
        }
    }

    @Test
    public void pwdInRoot() {
        try{
            fs.setCurrentDir(fs.getRoot());
            assertEquals("/", pwdService.execute(operands, flags));
        }catch (FileSystemException e){
            fail("Unexpected FileSystemException: " + e.getMessage());
        }
    }

    @Test
    public void pwdInInnerFolders() {
        try{
            fs.setCurrentDir(ciaoDir);
            assertEquals("/ciao/", pwdService.execute(operands, flags));
            fs.setCurrentDir(testDir);
            assertEquals("/test/", pwdService.execute(operands, flags));
            fs.setCurrentDir(cartellaDir);
            assertEquals("/test/cartella/", pwdService.execute(operands, flags));
            fs.setCurrentDir(lolDir);
            assertEquals("/test/cartella/lol/", pwdService.execute(operands, flags));
        }catch (FileSystemException e){
            fail("Unexpected FileSystemException: " + e.getMessage());
        }
    }

    @Test
    public void pwdExceptionIfParams(){
        try{
            fs.setCurrentDir(fs.getRoot());
            assertThrows(TooManyArgumentsException.class,() -> pwdService.execute(operands, List.of("a")));
            assertThrows(TooManyArgumentsException.class,() -> pwdService.execute(List.of("a"), flags));
            assertThrows(TooManyArgumentsException.class,() -> pwdService.execute(List.of("a"), List.of("a")));
            assertDoesNotThrow(() -> pwdService.execute(operands, flags));
        }catch (FileSystemException e){
            fail("Unexpected FileSystemException: " + e.getMessage());
        }
    }
}

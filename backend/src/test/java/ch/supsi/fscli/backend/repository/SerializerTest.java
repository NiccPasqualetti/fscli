package ch.supsi.fscli.backend.repository;

import ch.supsi.fscli.backend.service.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SerializerTest {
    private static final String TEST_PATH = System.getProperty("user.home")+"/mySerializableTestClass.txt";
    private final Path path = Paths.get(TEST_PATH);

    private Serializer serializerA, serializerB;
    private Filesystem filesystem;
    private MySerializableTestClass mySerializableTestClass;

    @BeforeEach
    public void setUp() throws IOException {
        RuntimeTypeAdapterFactory<INode> runtimeAdapter = RuntimeTypeAdapterFactory
                .of(INode.class, "node")
                .registerSubtype(DirectoryINode.class, "dir")
                .registerSubtype(FileINode.class, "file")
                .registerSubtype(SymlinkINode.class, "link");
        serializerA = new Serializer(runtimeAdapter, Filesystem.class);
        serializerB = new Serializer(MySerializableTestClass.class);

        filesystem = new Filesystem("Test class");
        mySerializableTestClass = new MySerializableTestClass();
    }

    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(path);
    }

    @Test
    public void writeFilesystem() throws IOException {
        assertThrows(FileNotFoundException.class, ()-> serializerA.write(filesystem));
        filesystem.setFileFullPathName(TEST_PATH);
        assertDoesNotThrow(() -> serializerA.write(filesystem));
        assertTrue(Files.exists(path), "File does not exist");
        assertTrue(Files.size(path) > 0, "File emtpy");
    }

    @Test
    public void writeMySerializableTestClass()throws IOException {
        assertThrows(FileNotFoundException.class, ()-> serializerB.write(mySerializableTestClass));
        mySerializableTestClass.setFileFullPathName(TEST_PATH);
        assertDoesNotThrow(() -> serializerB.write(mySerializableTestClass));
        assertTrue(Files.exists(path), "File does not exist");
        assertTrue(Files.size(path) > 0, "File emtpy");
    }

    @Test
    public void readFilesystem() throws IOException {
        filesystem.setFileFullPathName(TEST_PATH);
        serializerA.write(filesystem);
        File file = new File(TEST_PATH);
        Filesystem actual = (Filesystem) assertDoesNotThrow(()-> serializerA.read(file));
        assertEquals(filesystem.getFileFullPathName(), actual.getFileFullPathName());
    }

    @Test
    public void readMySerializableTestClass() throws IOException {
        mySerializableTestClass.setFileFullPathName(TEST_PATH);
        serializerB.write(mySerializableTestClass);
        File file = new File(TEST_PATH);
        MySerializableTestClass actual = (MySerializableTestClass) assertDoesNotThrow(()-> serializerB.read(file));
        assertEquals(mySerializableTestClass.getFileFullPathName(), actual.getFileFullPathName());
    }
}

class MySerializableTestClass implements Serializable {
    private String path;
    @Override
    public boolean isSaved() {
        return path != null;
    }

    @Override
    public String getFileFullPathName() {
        return path;
    }

    @Override
    public void setFileFullPathName(String fileFullPathName) {
        this.path = fileFullPathName;
    }
}

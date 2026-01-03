package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.controller.SerialEventHandler;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;
import ch.supsi.fscli.backend.repository.*;
import ch.supsi.fscli.backend.service.model.*;

import java.io.File;
import java.io.IOException;

public class SerialService implements SerialEventHandler {
    private final CreateFilesystemService createFilesystemService;
    private final Serializer serializer;

    public SerialService(CreateFilesystemService createFilesystemService) {
        this.createFilesystemService = createFilesystemService;
        RuntimeTypeAdapterFactory<INode> runtimeAdapter = RuntimeTypeAdapterFactory
                .of(INode.class, "node")
                .registerSubtype(DirectoryINode.class, "dir")
                .registerSubtype(FileINode.class, "file")
                .registerSubtype(SymlinkINode.class, "link");

        this.serializer = new Serializer(runtimeAdapter, Filesystem.class);
    }

    @Override
    public void save() throws IOException {
        Serializable fileSystem = createFilesystemService.getFilesystem();
        serializer.write(fileSystem);
    }

    @Override
    public void saveAs(File file) throws IOException {
        Serializable fileSystem = createFilesystemService.getFilesystem();
        fileSystem.setFileFullPathName(file.getAbsolutePath());
        serializer.write(fileSystem);
    }

    @Override
    public void open(File file) throws IOException {
        Filesystem tmp = (Filesystem) serializer.read(file);
        rebuildParentStructure(tmp.getRoot(), null);
        try{
            tmp.setCurrentDir(tmp.getRoot());
        } catch (NoSuchFileOrDirectoryException e) {
            throw new IOException();
        }
        createFilesystemService.setFilesystem(tmp);
    }

    public void rebuildParentStructure(INode node, DirectoryINode parent) {
        node.setParent(parent);
        if (node instanceof DirectoryINode dir) {
            for (INode child : dir.getChildren().values()) {
                rebuildParentStructure(child, dir);
            }
        }
    }

    public boolean isAlreadySaved(){
        return createFilesystemService.getFilesystem().isSaved();
    }
}

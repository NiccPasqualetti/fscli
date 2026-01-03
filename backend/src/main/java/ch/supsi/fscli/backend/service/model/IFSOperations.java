package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.*;

import java.util.List;

/**
 * Defines filesystem primitive operations like createInode, removeInode, ...
 */
public interface IFSOperations {

    DirectoryINode getRoot();

    FileINode createFile(DirectoryINode parent, String name) throws FileAlreadyExistsException, InvalidFilenameException;

    DirectoryINode createDirectory(DirectoryINode parent, String name) throws FileAlreadyExistsException, InvalidFilenameException;

    SymlinkINode createSymlink(INode destination, DirectoryINode parent, String linkName) throws FileAlreadyExistsException, InvalidFilenameException;

    INode createHardlink(IHardLinkable destination, DirectoryINode parent, String linkName) throws SameFileException, InvalidFilenameException, FileAlreadyExistsException, NoSuchFileOrDirectoryException;

    void removeINode(DirectoryINode parent, String name) throws DirectoryNotEmptyException, NoSuchFileOrDirectoryException;

    INode resolvePath(List<String> path) throws NoSuchFileOrDirectoryException;

    INode resolvePath(String path) throws NoSuchFileOrDirectoryException;

     void setCurrentDir(DirectoryINode directory) throws NoSuchFileOrDirectoryException;

    DirectoryINode getCurrentDir();

    boolean inside(INode element, DirectoryINode directory);

    void move(INode source, DirectoryINode destination) throws FileSystemException;

}

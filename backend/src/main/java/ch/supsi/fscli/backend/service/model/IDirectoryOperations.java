package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.DirectoryNotEmptyException;
import ch.supsi.fscli.backend.exceptions.FileAlreadyExistsException;
import ch.supsi.fscli.backend.exceptions.InvalidFilenameException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;

import java.util.Map;

public interface IDirectoryOperations {

    Map<String,INode> getChildren();

    void addChild(INode child, String name) throws FileAlreadyExistsException, InvalidFilenameException;

    INode searchInFolder(String name);

    void removeChild(String name) throws NoSuchFileOrDirectoryException,DirectoryNotEmptyException;

}

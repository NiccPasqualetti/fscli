package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.DirectoryNotEmptyException;
import ch.supsi.fscli.backend.exceptions.FileAlreadyExistsException;
import ch.supsi.fscli.backend.exceptions.InvalidFilenameException;
import ch.supsi.fscli.backend.exceptions.NoSuchFileOrDirectoryException;

import java.util.*;

public class DirectoryINode extends INode implements IDirectoryOperations {

    private final Map<String, INode> children;

    public DirectoryINode(Long id, DirectoryINode parent) {
        super(id, INodeType.DIRECTORY, parent);
        this.children = new HashMap<>();
    }

    /**
     * Returns an immutable copy of directory contents
     */
    @Override
    public Map<String,INode> getChildren() {
        return Map.copyOf(children);
    }

    /**
     * Adds an item to a directory, invoked every time an inode of any type is created
     * @param child inode object
     * @param name name of the object on the filesystem
     */
    @Override
    public void addChild(INode child, String name) throws FileAlreadyExistsException {
        if (this.children.containsKey(name)) {
            throw new FileAlreadyExistsException();
        }
        this.children.put(name, child);
    }

    /**
     * Removes an item in a directory
     * @param name item name
     */
    @Override
    public void removeChild(String name) throws NoSuchFileOrDirectoryException, DirectoryNotEmptyException {

        if (this.children.get(name) == null) {
            throw new NoSuchFileOrDirectoryException();
        }
        if (this.children.get(name) instanceof IHardLinkable child) {
            child.decreaseReferenceCount();
        }
            this.children.remove(name);
    }

    public void renameChild(String oldName, String newName) throws InvalidFilenameException, FileAlreadyExistsException {
        if (Filesystem.invalidFilename(newName)) {
            throw new InvalidFilenameException();
        }
        INode item = this.children.remove(oldName);
        this.addChild(item, newName);
    }


    /**
     * This method searches in the folder for a specific folder / file
     * <p>
     * If not found, returns null
     * @param name The key
     * @return The INode
     */
    @Override
    public INode searchInFolder(String name) {
        return children.getOrDefault(name, null);
    }
}

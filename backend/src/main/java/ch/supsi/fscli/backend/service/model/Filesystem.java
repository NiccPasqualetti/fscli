package ch.supsi.fscli.backend.service.model;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.repository.Serializable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Filesystem implements IFSOperations, Serializable {

    private final String filesystemName;
    private final DirectoryINode root;
    private DirectoryINode currentDir;
    private long nextId = 1L;
    private String fileFullPathName;

    public static final char SEPARATOR = '/';
    public static final String ROOT = "/";
    public static final String CURRENT_DIR_SYMBOL = ".";
    public static final String PARENT_DIR_SYMBOL = "..";
    public static final String FORBIDDEN_CHARACTERS = "\\/+*:|?<>";


    /**
     * Constructs a {@link Filesystem} instance and initializes its root directory.
     * @param name a string used to identify the filesystem instance
     * @throws IllegalArgumentException if the provided name is blank
     */
    public Filesystem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Filesystem name must not be blank");
        }
        this.filesystemName = name;
        this.root = new DirectoryINode(nextId++, null);
        this.currentDir = root;
    }

    public String getFilesystemName() {
        return filesystemName;
    }

    @Override
    public DirectoryINode getRoot() {
        return root;
    }

    /**
     * Checks whether a filename contains forbidden characters.
     * @param name the filename to validate
     * @return true if the filename is invalid, false otherwise
     */
    public static boolean invalidFilename(String name) {
        if (Filesystem.FORBIDDEN_CHARACTERS.chars()
                .anyMatch(ch -> name.indexOf(ch) >= 0)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a {@link FileINode} instance and adds it to the {@link DirectoryINode} instance passed as parameter "parent"
     * <p>
     * Name is used as the key in the parent {@link DirectoryINode} map
     * @param parent
     * @param name
     * @return
     * @throws FileAlreadyExistsException
     * @throws InvalidFilenameException
     */
    @Override
    public FileINode createFile(DirectoryINode parent, String name) throws FileAlreadyExistsException, InvalidFilenameException {
        if (invalidFilename(name)) {
            throw new InvalidFilenameException();
        }
        FileINode fileINode = new FileINode(nextId++, parent);
        parent.addChild(fileINode, name);
        return fileINode;
    }
    /**
     * Creates a {@link DirectoryINode} instance and adds it to the {@link DirectoryINode} instance passed as parameter "parent"
     * <p>
     * Name is used as the key in the parent {@link DirectoryINode} map
     * @param parent
     * @param name
     * @return The directory INode
     * @throws FileAlreadyExistsException
     * @throws InvalidFilenameException
     */
    @Override
    public DirectoryINode createDirectory(DirectoryINode parent, String name) throws FileAlreadyExistsException, InvalidFilenameException {
        if (invalidFilename(name)) {
            throw new InvalidFilenameException();
        }
        DirectoryINode directoryINode = new DirectoryINode(nextId++, parent);
        parent.addChild(directoryINode, name);
        return directoryINode;
    }

    /**
     * @param destination Where the link points to
     * @param parent Where the link itself is located
     * @param linkName Name of the link
     */
    @Override
    public SymlinkINode createSymlink(INode destination, DirectoryINode parent, String linkName) throws FileAlreadyExistsException {
        SymlinkINode symlinkINode = new SymlinkINode(nextId++, parent, destination);
        parent.addChild(symlinkINode, linkName);
        return symlinkINode;
    }

    /**
     * @param destination Where the link points to
     * @param parent Where the link itself is located
     * @param linkName Name of the link
     */
    @Override
    public INode createHardlink(IHardLinkable destination, DirectoryINode parent, String linkName) throws SameFileException, FileAlreadyExistsException, NoSuchFileOrDirectoryException {

        if (destination == null) {
            throw new NoSuchFileOrDirectoryException();
        }
        INode existing = parent.searchInFolder(linkName);
        if (existing != null && existing.getId() == destination.getId()) {
            throw new SameFileException();
        }
        if (parent.getChildren().containsKey(linkName)) {
            throw new FileAlreadyExistsException();
        }

        destination.increaseReferenceCount();
        parent.addChild((INode) destination, linkName);
        return (INode) destination;
    }

    /**
     * Deletes the specified INode, can be of type directory / file
     * <p>
     * If it is a directory, the remove method of {@link DirectoryINode}
     * checks if it is empty, removes only if empty
     */
    @Override
    public void removeINode(DirectoryINode parent, String name) throws DirectoryNotEmptyException, NoSuchFileOrDirectoryException {
        INode target = parent.searchInFolder(name);
        if (target == null) {
            throw new NoSuchFileOrDirectoryException();
        }
        parent.removeChild(name);
    }

    public static LinkedList<String> generateFilesystemPath(String path) {
        LinkedList<String> pathAsList = new LinkedList<>();
        Path filePath = Paths.get(path);

        for (Path item : filePath) {
            pathAsList.add(item.toString());
        }
        if (filePath.isAbsolute()) {
            pathAsList.addFirst(ROOT);
        }
        return pathAsList;
    }

    /**
     * This method searches for the INode at the specified path
     * @param path Accepts a String representing the full path to the INode ("/a/b/c")
     */
    @Override
    public INode resolvePath(String path) throws NoSuchFileOrDirectoryException {
        return resolvePath(generateFilesystemPath(path));
    }

    /**
     * This method searches for the INode at the specified path.
     * @param path Path to INode, as a List of String
     * @return the {@link INode} found at the end of the path
     */
    @Override
    public INode resolvePath(List<String> path) throws NoSuchFileOrDirectoryException {
        DirectoryINode startingDir;
        LinkedList<String> newPath = new LinkedList<>(path);
        int relativeLevels = 0;

        switch (path.get(0)) {
            case ROOT -> {
                startingDir = root;
                relativeLevels = 1;
            }
            case CURRENT_DIR_SYMBOL -> {
                startingDir = currentDir;
                relativeLevels = 1;
            }
            case PARENT_DIR_SYMBOL -> {
                startingDir = currentDir;
                int index = 0;

                while (index < path.size() && PARENT_DIR_SYMBOL.equals(path.get(index))) {
                    DirectoryINode parent = startingDir.getParent();
                    if (parent != null) {
                        startingDir = parent;
                    } else {
                        startingDir = root;
                    }
                    index++;
                }
                relativeLevels = index;
            }
            default -> startingDir = currentDir;
        }

        while (relativeLevels > 0) {
            newPath.removeFirst();
            relativeLevels--;
        }

        return resolvePathLocal(newPath, startingDir);
    }

    /**
     * checks if an element is inside a folder
     * @param element
     * @param directory
     * @return
     */
    @Override
    public boolean inside(INode element, DirectoryINode directory) {
        while (element.getParent() != null) {
            if (directory.getChildren().containsKey(element.getName())) {
                return true;
            }
            element = element.getParent();
        }
        return false;
    }

    /**
     * move an element to a destination
     * @param source
     * @param destination
     */
    @Override
    public void move(INode source, DirectoryINode destination) throws FileSystemException {
        destination.addChild(source, source.getName());
        source.getParent().removeChild(source.getName());
    }

    /**
     * Resolves a path relative to a starting directory.
     * Returns the last {@link INode} reached when walking the path segments.
     * @param path the path expressed as a list of segments
     * @param startingDir the directory from which resolution begins
     * @return the {@link INode} located at the provided path
     * @throws NoSuchFileOrDirectoryException if any segment cannot be resolved
     */
    public INode resolvePathLocal(LinkedList<String> path, DirectoryINode startingDir) throws NoSuchFileOrDirectoryException {

        DirectoryINode intermediateResult = startingDir;
        INode tempValue;

        for (int i = 0; i < path.size(); i++) {
            tempValue = intermediateResult.searchInFolder(path.get(i));
            if (tempValue == null) {
                throw new NoSuchFileOrDirectoryException();
            }
            else if (tempValue.getType() == INodeType.DIRECTORY) {
                intermediateResult = (DirectoryINode) tempValue;
            }
            else {
                return tempValue;
            }
        }
        return intermediateResult;
    }

    @Override
    public void setCurrentDir(DirectoryINode directory) throws NoSuchFileOrDirectoryException {
        if (directory == null) {
            throw new NoSuchFileOrDirectoryException();
        }
        this.currentDir = directory;
    }

    @Override
    public DirectoryINode getCurrentDir() {
        return currentDir;
    }


    @Override
    public boolean isSaved() {
        return fileFullPathName != null;
    }

    @Override
    public String getFileFullPathName() {
        return fileFullPathName;
    }

    @Override
    public void setFileFullPathName(String fileFullPathName) {
        this.fileFullPathName = fileFullPathName;
    }
}

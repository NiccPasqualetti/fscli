package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.List;
import java.util.Map;

public class LsService extends AbstractCommand {
    private static final String MY_TAB = "    ";
    private final CreateFilesystemService createFilesystemService;

    public LsService(String description, CreateFilesystemService createFilesystemService) {
        super("ls", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * @param operands
     * @param flags the -i flag show the inode of the file/directory
     * @return the list of files, directories and symlink in the directory
     * @throws MissingOperandsException
     * @throws TooManyArgumentsException
     * @throws NoSuchFileOrDirectoryException
     */
    public String execute(List < String > operands, List < String > flags) throws FileSystemException {
        if (flags.size() > 1)
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();

        boolean showMultipleDirecories = operands.size() > 1;
        boolean showInode = false;
        if (!flags.isEmpty()) {
            String option = flags.get(0);
            if (option.equals("i"))
                showInode = true;
            else
                throw new InvalidOptionException();
        }

        if (operands.isEmpty())
            operands.add(".");
        StringBuilder sb = new StringBuilder();
        for (String path: operands) {
            List <String> pathAsList = Filesystem.generateFilesystemPath(path);
            String dirName = "Unknown";
            DirectoryINode dir;
            if (pathAsList.isEmpty())
                dir = filesystem.getCurrentDir();
            else {
                INode tempNode = filesystem.resolvePath(pathAsList);
                while (tempNode instanceof SymlinkINode symlinkINode) {
                    tempNode = symlinkINode.getDestination();
                }
                if (tempNode.getType()== INodeType.FILE) {
                    return tempNode.getName();
                }
                dir = (DirectoryINode) tempNode;
            }

            DirectoryINode parent = dir.getParent();
            if (parent != null) {
                for (var parentChild: dir.getParent().getChildren().entrySet())
                    if (parentChild.getValue().getId() == dir.getId())
                        dirName = parentChild.getKey();
            } else {
                dirName = "/";
            }

            if (showMultipleDirecories)
                sb.append(dirName).append(":\n");

            for (var child: dir.getChildren().entrySet()) {
                INode node = child.getValue();
                if (showInode)
                    sb.append(node.getId()).append(" ");

                if(node instanceof SymlinkINode symlinkNode) {
                    INode symLinkDest = symlinkNode.getDestination();
                    try{
                        String destPath = getFullPath(symLinkDest);
                        sb.append(child.getKey()).append(" -> ").append(destPath);
                    }catch (Exception e){
                        sb.append(child.getKey()).append(" -> ").append(" err");
                    }
                } else {
                    sb.append(child.getKey());
                }

                sb.append(MY_TAB);
            }

            if (showMultipleDirecories)
                sb.append("\n\n");
        }
        return sb.toString();
    }

    /**
     * Returns the absolute path of an INode (file or directory).
     * If the node is the root, it returns "/".
     * @param node the node whose path is to be determined
     * @return the absolute path as a string
     */
    private String getFullPath(INode node) throws FileSystemException {
        if (node == null)
            throw new NoSuchFileOrDirectoryException();

        // is root
        if (node.getParent() == null)
            return "/";

        StringBuilder result = new StringBuilder();
        INode current = node;

        while (current != null) {
            DirectoryINode parent = current.getParent();
            // raggiunta la root
            if (parent == null)
                break;


            String name = null;
            for (Map.Entry<String, INode> entry : parent.getChildren().entrySet()) {
                if (entry.getValue().getId() == current.getId()) {
                    name = entry.getKey();
                    break;
                }
            }

            if (name == null)
                name = "Unknown";

            result.insert(0, name);
            result.insert(0, "/");

            current = parent;
        }

        if (result.isEmpty())
            return "/";

        return result.toString();
    }
}

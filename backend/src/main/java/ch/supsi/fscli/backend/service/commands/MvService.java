package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.LinkedList;
import java.util.List;

public class MvService extends AbstractCommand {
    private final CreateFilesystemService createFilesystemService;

    public MvService(String description, CreateFilesystemService createFilesystemService) {
        super("mv", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * move files or directories to a new destination, syntax: mv SRC1 SRC2 ... DST
     * rename a file or directory, syntax:  OLD_NAME NEW_NAME
     *
     * @param operands
     * @param flags
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {

        if (operands.size() < 2) {
            throw new MissingOperandsException();
        }

        if (!flags.isEmpty()) {
            throw new TooManyArgumentsException();
        }

        Filesystem filesystem = createFilesystemService.getFilesystem();
        LinkedList<String> operandsList = new LinkedList<>(operands);

        if (operandsList.size() == 2) {
            String sourcePath = operandsList.getFirst();
            String destPath = operandsList.getLast();

            INode source = filesystem.resolvePath(sourcePath);
            try {
                INode destNode = filesystem.resolvePath(destPath);

                while (destNode instanceof SymlinkINode symlinkINode) {
                    destNode = symlinkINode.getDestination();
                }

                if (destNode.getType() != INodeType.DIRECTORY) {
                    throw new FileAlreadyExistsException();
                }

                DirectoryINode destDir = (DirectoryINode) destNode;

                if (source instanceof DirectoryINode sourceDir && filesystem.inside(destDir,sourceDir)) {
                    throw new InvalidDirectoryException();
                }

                if (destDir.searchInFolder(source.getName()) != null) {
                    throw new FileAlreadyExistsException();
                }
                filesystem.move(source, destDir);
            } catch (NoSuchFileOrDirectoryException e) {
                LinkedList<String> destComponents = Filesystem.generateFilesystemPath(destPath);
                String newName = destComponents.removeLast();

                if (filesystem.resolvePath(destComponents) == null) {
                    throw new NoSuchFileOrDirectoryException();
                }

                DirectoryINode sourceParent = source.getParent();

                if (Filesystem.invalidFilename(newName)) {
                    throw new InvalidFilenameException();
                }

                if (sourceParent.searchInFolder(newName) != null) {
                    throw new FileAlreadyExistsException();
                }

                sourceParent.renameChild(source.getName(), newName);
            }


        } else {
            String destPath = operandsList.removeLast();
            INode destNode = filesystem.resolvePath(destPath);

            if (!(destNode instanceof DirectoryINode destDir)) {
                throw new WrongFiletypeException();
            }

            for (String sourcePath : operandsList) {
                INode source = filesystem.resolvePath(sourcePath);

                if (source instanceof DirectoryINode sourceDir && filesystem.inside(destDir, sourceDir)) {
                    throw new InvalidDirectoryException();
                }

                if (destDir.searchInFolder(source.getName()) != null) {
                    throw new FileAlreadyExistsException();
                }

                filesystem.move(source, destDir);
            }
        }

        return null;
    }
}

package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.LinkedList;
import java.util.List;

public class CdService extends AbstractCommand {

    private final CreateFilesystemService createFilesystemService;

    public CdService(String description, CreateFilesystemService createFilesystemService) {
        super("cd", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * Changes the current directory
     * @param operands destination directory
     * @param flags flags given to the program (ex. -l, -ls)
     * @throws NoSuchFileOrDirectoryException
     * @throws WrongFiletypeException
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if (!flags.isEmpty() || operands.size() > 1)
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();
        LinkedList<String> pathAsList;

        if (operands.isEmpty()) {
            pathAsList = Filesystem.generateFilesystemPath(Filesystem.ROOT);
        } else {
            pathAsList = Filesystem.generateFilesystemPath(operands.get(0));
        }

        INode destination = filesystem.resolvePath(pathAsList);

        if (destination.getType()== INodeType.DIRECTORY) {
            filesystem.setCurrentDir((DirectoryINode) destination);
        }
        else if (destination.getType() == INodeType.SYM_LINK) {
            filesystem.setCurrentDir((DirectoryINode) ((SymlinkINode) destination).getDestination());
        } else {
            throw new WrongFiletypeException();
        }
        return null;
    }
}

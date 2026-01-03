package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.LinkedList;
import java.util.List;

public class TouchService extends AbstractCommand {

    private final CreateFilesystemService createFilesystemService;

    public TouchService(String description, CreateFilesystemService createFilesystemService) {
        super("touch", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * Creates files at the provided paths within the current {@link Filesystem} instance.
     * @param operands absolute or relative paths for the files to create
     * @param flags flags given to the program (must be empty)
     * @throws InvalidFilenameException if a name contains forbidden characters
     * @throws FileAlreadyExistsException if a target already exists
     * @throws MissingOperandsException if no paths are supplied
     * @throws TooManyArgumentsException if flags are provided
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if(operands.isEmpty())
            throw new MissingOperandsException();
        if(!flags.isEmpty())
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();
        for(String operand : operands){
            LinkedList<String> pathAsList = Filesystem.generateFilesystemPath(operand);

            if (!pathAsList.getFirst().equals(Filesystem.ROOT)) {
                pathAsList.addFirst(".");
            }
            String name = pathAsList.getLast();
            pathAsList.removeLast();
            DirectoryINode parent = (DirectoryINode) filesystem.resolvePath(pathAsList);
            filesystem.createFile(parent, name);

        }
        return null;
    }
}

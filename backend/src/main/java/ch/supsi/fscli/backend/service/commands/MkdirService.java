package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.List;

public class MkdirService extends AbstractCommand {
    private final CreateFilesystemService createFilesystemService;

    public MkdirService(String description, CreateFilesystemService createFilesystemService) {
        super("mkdir", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * Adds a directory to the current {@link Filesystem} instance
     * @param operands the quantity on which an operation is to be done.
     * @param flags flags given to the program (ex. -l, -ls)
     * @throws FileAlreadyExistsException
     * @throws InvalidFilenameException
     * @throws TooManyArgumentsException
     * @throws MissingOperandsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if(operands.isEmpty())
            throw new MissingOperandsException();
        if(!flags.isEmpty())
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();
        for(String path : operands){
            List<String> pathAsList = Filesystem.generateFilesystemPath(path);
            int lastIndex = pathAsList.size() - 1;
            String name = pathAsList.get(lastIndex);
            pathAsList.remove(lastIndex);
            DirectoryINode parent;
            if(pathAsList.isEmpty())
                parent = filesystem.getCurrentDir();
            else
                parent = (DirectoryINode) filesystem.resolvePath(pathAsList);
            filesystem.createDirectory(parent, name);
        }
        return null;
    }
}

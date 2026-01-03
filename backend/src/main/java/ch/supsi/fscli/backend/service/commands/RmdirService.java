package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.*;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.INode;

import java.util.List;

public class RmdirService extends AbstractCommand {
    private final CreateFilesystemService createFilesystemService;


    public RmdirService(String description, CreateFilesystemService createFilesystemService) {
        super("rmdir", description);
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * Removes the directory to the current {@link Filesystem} instance
     *
     * @param operands the quantity on which an operation is to be done.
     * @param flags    flags given to the program (ex. -l, -ls)
     * @throws NoSuchFileOrDirectoryException
     * @throws DirectoryNotEmptyException
     * @throws MissingOperandsException
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if (operands.isEmpty())
            throw new MissingOperandsException();
        if (!flags.isEmpty())
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();
        for (String path : operands) {

            List<String> pathAsList = Filesystem.generateFilesystemPath(path);
            int lastIndex = pathAsList.size() - 1;
            String name = pathAsList.get(lastIndex);
            pathAsList.remove(lastIndex);
            DirectoryINode parent;

            if (pathAsList.isEmpty()) {
                parent = filesystem.getCurrentDir();
            } else {
                parent = (DirectoryINode) filesystem.resolvePath(pathAsList);
            }
            INode target = parent.searchInFolder(name);
            if (target == null) {
                throw new NoSuchFileOrDirectoryException();
            }
            if (!(target instanceof DirectoryINode targetDir)) {
                throw new WrongFiletypeException();
            }
            if (!targetDir.getChildren().isEmpty()) {
                throw new DirectoryNotEmptyException();
            }
            parent.removeChild(name);
        }
        return null;
    }
}

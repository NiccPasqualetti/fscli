package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.InvalidOptionException;
import ch.supsi.fscli.backend.exceptions.MissingOperandsException;
import ch.supsi.fscli.backend.exceptions.TooManyArgumentsException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.*;

import java.util.LinkedList;
import java.util.List;

public class LnService extends AbstractCommand {
    private final CreateFilesystemService createFilesystemService;

    public LnService(String description, CreateFilesystemService createFilesystemService) {
        super("ln", description);
        this.createFilesystemService = createFilesystemService;
    }


    /**
     * Create soft and hard links, SYNTAX: ln [-s, -P] TARGET LINK_NAME
     * @param operands
     * @param flags
     *
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {

        if(operands.size() != 2) {
            if (operands.size() > 2) {
                throw new TooManyArgumentsException();
            } else {
                throw new MissingOperandsException();
            }
        }

        if (flags.size() > 1) {
            throw new TooManyArgumentsException();
        }

        Filesystem filesystem = createFilesystemService.getFilesystem();

        String destination = operands.get(0);
        INode destinationPath = filesystem.resolvePath(destination);

        if (!flags.isEmpty() && flags.get(0).equals("s")) {
            while(destinationPath instanceof SymlinkINode symlinkINode) {
                destinationPath = symlinkINode.getDestination();
            }
        }

        LinkedList<String> linkLocation = Filesystem.generateFilesystemPath(operands.get(1));
        if (!linkLocation.getFirst().equals(Filesystem.ROOT)) {
            linkLocation.addFirst(".");
        }
        String linkName = linkLocation.get(1);
        linkLocation.removeLast();
        DirectoryINode linkLocationDir = (DirectoryINode) filesystem.resolvePath(linkLocation);

        if ((flags.isEmpty()) && destinationPath instanceof IHardLinkable destItem) {
            filesystem.createHardlink(destItem, linkLocationDir, linkName);
        } else if (flags.get(0).equals("s")) {
            filesystem.createSymlink(destinationPath, linkLocationDir, linkName);
        } else {
            throw new InvalidOptionException();
        }

        return null;
    }
}

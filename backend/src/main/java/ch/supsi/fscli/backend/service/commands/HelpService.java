package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.TooManyArgumentsException;

import java.util.List;

public class HelpService extends AbstractCommand {
    private final List<String> descriptions;

    public HelpService(String description, List<String> descriptions) {
        super("help", description);
        this.descriptions = descriptions;
    }


    /**
     * Return the commands description in a string
     * @param operands
     * @param flags
     * @return the commands descriptions
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if(!operands.isEmpty() || !flags.isEmpty())
            throw new TooManyArgumentsException();

        StringBuilder sb = new StringBuilder();
        for(String desc : descriptions)
            sb.append(desc).append("\n");
        return sb.toString();
    }
}

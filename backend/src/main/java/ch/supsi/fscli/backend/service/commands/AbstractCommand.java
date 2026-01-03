package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;

import java.util.List;

public abstract class AbstractCommand implements Command {
    private final String name;
    private final String description;

    /**
     * @param name of the command
     * @param description is the tag for the translations
     */
    public AbstractCommand(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * What the command must do
     * @param operands
     * @param flags
     * @return null if the command goes well or something if the file return something on stdout
     * @throws FileSystemException
     */
    public abstract String execute(List<String> operands, List<String> flags) throws FileSystemException;
}

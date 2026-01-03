package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;

import java.util.List;

public interface Command {
    String getName();
    String getDescription();
    String execute(List<String> operands, List<String> flags) throws FileSystemException;
}

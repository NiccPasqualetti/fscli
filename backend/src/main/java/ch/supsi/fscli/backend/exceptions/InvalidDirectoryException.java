package ch.supsi.fscli.backend.exceptions;

public class InvalidDirectoryException extends FileSystemException {
    public InvalidDirectoryException() {
        super("except.invalidDirectory");
    }
}

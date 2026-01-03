package ch.supsi.fscli.backend.exceptions;

public class InvalidOptionException extends FileSystemException {
    public InvalidOptionException() {
        super("except.invalidOption");
    }
}

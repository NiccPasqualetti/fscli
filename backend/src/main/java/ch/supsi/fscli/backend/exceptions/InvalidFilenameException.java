package ch.supsi.fscli.backend.exceptions;

public class InvalidFilenameException extends FileSystemException {
    public InvalidFilenameException() {
        super("except.invalidFilename");
    }
}

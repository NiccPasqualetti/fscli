package ch.supsi.fscli.backend.exceptions;

public class DirectoryNotEmptyException extends FileSystemException {
    public DirectoryNotEmptyException() {
        super("except.directoryNotEmpty");
    }
}

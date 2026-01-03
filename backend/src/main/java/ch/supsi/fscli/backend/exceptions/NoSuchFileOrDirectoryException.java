package ch.supsi.fscli.backend.exceptions;

public class NoSuchFileOrDirectoryException extends FileSystemException {
    public NoSuchFileOrDirectoryException() {
        super("except.noSuchFileOrDirectory");
    }
}

package ch.supsi.fscli.backend.exceptions;

public class FileSystemException extends Exception {
    public FileSystemException(String message) {
        super(message);
    }
    public FileSystemException() {}
}

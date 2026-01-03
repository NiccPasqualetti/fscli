package ch.supsi.fscli.backend.exceptions;

public class FileAlreadyExistsException extends FileSystemException {
    public FileAlreadyExistsException() {
        super("except.fileAlreadyExists");
    }
}

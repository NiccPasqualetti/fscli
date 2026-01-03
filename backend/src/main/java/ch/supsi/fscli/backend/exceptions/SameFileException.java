package ch.supsi.fscli.backend.exceptions;
/**
 * When you try to move a file/directory onto itself
 */
public class SameFileException extends FileSystemException {
    public SameFileException() {
        super("except.sameFile");
    }
}

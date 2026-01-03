package ch.supsi.fscli.backend.exceptions;

public class WrongFiletypeException extends FileSystemException {
    public WrongFiletypeException() {
        super("except.wrongFiletype");
    }
}

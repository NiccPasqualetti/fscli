package ch.supsi.fscli.backend.exceptions;

public class MissingOperandsException extends FileSystemException {
    public MissingOperandsException() {
        super("except.missingOperands");
    }
}

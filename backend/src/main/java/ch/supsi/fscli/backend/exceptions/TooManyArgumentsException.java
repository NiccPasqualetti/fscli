package ch.supsi.fscli.backend.exceptions;

public class TooManyArgumentsException extends FileSystemException {
    public TooManyArgumentsException() {
        super("except.tooManyArguments");
    }
}

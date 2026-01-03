package ch.supsi.fscli.frontend.controller;

public interface CommandEventHandler extends EventHandler {
    void executeCommand(String input);
}

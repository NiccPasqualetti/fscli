package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.EventManager;
import ch.supsi.fscli.frontend.notification.EventType;

public class FilesystemController implements CommandEventHandler {
    private final FilesystemModel filesystemModel;
    private final EventManager eventManager;

    public FilesystemController(FilesystemModel filesystemModel, EventManager eventManager) {
        this.filesystemModel = filesystemModel;
        this.eventManager = eventManager;
    }

    @Override
    public void executeCommand(String input) {
        this.filesystemModel.executeCommand(input);
        this.eventManager.notify(EventType.OUTPUT);
        this.eventManager.notify(EventType.LOG);
    }

}

package ch.supsi.fscli.frontend.controller;

public interface PersistanceEventHandler extends EventHandler {
    void save();
    void saveAs();
    void open();
}

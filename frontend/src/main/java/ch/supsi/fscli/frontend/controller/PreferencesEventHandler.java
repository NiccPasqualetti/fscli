package ch.supsi.fscli.frontend.controller;

public interface PreferencesEventHandler extends EventHandler{
    void openPreferencesMenu();
    void savePreferencesToFile();
    void setPreferencesToView();
    void cancelAndClose();
}

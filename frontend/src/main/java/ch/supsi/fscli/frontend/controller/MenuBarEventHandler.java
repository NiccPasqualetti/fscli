package ch.supsi.fscli.frontend.controller;

public interface MenuBarEventHandler extends FileSystemEventHandler, PersistanceEventHandler, ExitEventHandler, PreferencesEventHandler {
    void showAbout();
    void showHelp();
}

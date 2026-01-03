package ch.supsi.fscli.backend.controller;

import java.io.File;
import java.io.IOException;

public interface SerialEventHandler {
    void save() throws IOException;
    void saveAs(File file) throws IOException;
    void open(File file) throws IOException;
    boolean isAlreadySaved();
}

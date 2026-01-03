package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.SerialService;

import java.io.File;
import java.io.IOException;

public class SerialController implements SerialEventHandler {
    private final SerialService serialService;

    public SerialController(SerialService serialService) {
        this.serialService = serialService;
    }

    @Override
    public void save() throws IOException {
        this.serialService.save();
    }

    @Override
    public void saveAs(File file) throws IOException {
        this.serialService.saveAs(file);
    }

    @Override
    public void open(File file) throws IOException {
        this.serialService.open(file);
    }

    @Override
    public boolean isAlreadySaved() {
        return this.serialService.isAlreadySaved();
    }
}

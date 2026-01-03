package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.EventManager;
import ch.supsi.fscli.frontend.notification.EventType;
import ch.supsi.fscli.frontend.view.AboutDialog;
import ch.supsi.fscli.frontend.view.ExitDialog;
import ch.supsi.fscli.frontend.view.HelpDialog;
import ch.supsi.fscli.frontend.view.PreferencesMenuView;
import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.File;

public class MenuBarController implements MenuBarEventHandler{
    private final FilesystemModel filesystemModel;
    private final EventManager eventManager;

    private final PreferencesMenuView preferencesView;
    private final PreferencesController preferencesController;
    private AboutDialog aboutDialog;

    public MenuBarController(FilesystemModel filesystemModel, EventManager eventManager,
                             PreferencesController preferencesController, PreferencesMenuView preferencesView) {
        this.filesystemModel = filesystemModel;
        this.eventManager = eventManager;
        this.preferencesController = preferencesController;
        this.preferencesView = preferencesView;
        setupPreferencesEventHandlers();
    }

    @Override
    public void exit() {
        if(!filesystemModel.isFilesystemInitialized())
            Platform.exit();
        else
            new ExitDialog().showAndWait().ifPresent(result -> {
                if(result)
                    Platform.exit();
            });
    }

    @Override
    public void createFileSystem() {
        this.filesystemModel.createFileSystem();
        this.eventManager.notifyAllSubscribers();
    }

    @Override
    public void save() {
        if(filesystemModel.isAlreadySaved())
            filesystemModel.save();
        else
            saveAs();

        this.eventManager.notify(EventType.LOG);
    }

    @Override
    public void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FileSystem file", "*.fsf"));
        File file = fileChooser.showSaveDialog(null);

        if(file != null) {
            filesystemModel.saveAs(file);
            this.eventManager.notify(EventType.LOG);
        }
    }

    @Override
    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FileSystem file", "*.fsf"));
        File file = fileChooser.showOpenDialog(null);

        if(file != null) {
            filesystemModel.open(file);
            this.eventManager.notifyAllSubscribers();
        }
    }

    private void setupPreferencesEventHandlers() {
        preferencesView.getSaveButton().setOnAction(event -> savePreferencesToFile());
        preferencesView.getCancelButton().setOnAction(event -> cancelAndClose());
    }

    @Override
    public void openPreferencesMenu() {
        setPreferencesToView();
        preferencesView.getStage().show();
    }

    @Override
    public void savePreferencesToFile() {
        preferencesController.savePreferences(
                preferencesView.getSelectedLanguage(),
                preferencesView.getLogAreaFont(),
                preferencesView.getOutputAreaFont(),
                preferencesView.getCmdLineFont(),
                preferencesView.getLogAreaLines(),
                preferencesView.getOutputAreaLines(),
                preferencesView.getCmdLineColumns()
        );
        eventManager.notify(EventType.LOG, "preferences.saved.message");
        preferencesView.getStage().close();
    }

    @Override
    public void setPreferencesToView() {
        preferencesView.setLogAreaLines(preferencesController.getLogAreaLines());
        preferencesView.setOutputAreaLines(preferencesController.getOutputAreaLines());
        preferencesView.setCmdLineColumns(preferencesController.getCmdLineColumns());
        preferencesView.setLogAreaFont(preferencesController.getLogAreaFont());
        preferencesView.setOutputAreaFont(preferencesController.getOutputAreaFont());
        preferencesView.setCmdLineFont(preferencesController.getCmdLineFont());
        preferencesView.setSelectedLanguage(preferencesController.getCurrentLanguage());
    }

    @Override
    public void cancelAndClose() {
        preferencesView.getStage().close();
        setPreferencesToView();
        preferencesView.markAsSaved();
    }

    @Override
    public void showAbout() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog();
        }
        aboutDialog.show();
        aboutDialog.toFront();
    }

    @Override
    public void showHelp() {
        HelpDialog.show(null);
    }

}

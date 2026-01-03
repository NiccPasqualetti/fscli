package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.frontend.controller.*;
import ch.supsi.fscli.frontend.model.AbstractModel;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.Subscriber;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class MenuBarView implements ControlledFxView, Subscriber {
    private FilesystemModel filesystemModel;
    private MenuBarEventHandler menuBarEventHandler;
    private LanguageController languageController = LanguageController.getInstance();

    private MenuBar menuBar;

    private Menu fileMenu;
    private Menu editMenu;
    private Menu helpMenu;

    private MenuItem newMenuItem;
    private MenuItem openMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem saveAsMenuItem;
    private MenuItem quitMenuItem;
    private MenuItem preferencesMenuItem;
    private MenuItem aboutMenuItem;
    private MenuItem helpMenuItem;

    public MenuBarView() {
        newMenuItem = new MenuItem(languageController.translate("menu.file.new"));
        newMenuItem.setId("newMenuItem");
        openMenuItem = new MenuItem(languageController.translate("menu.file.open"));
        openMenuItem.setId("openMenuItem");
        saveMenuItem = new MenuItem(languageController.translate("menu.file.save"));
        saveMenuItem.setDisable(true);
        saveMenuItem.setId("saveMenuItem");
        saveAsMenuItem = new MenuItem(languageController.translate("menu.file.saveAs"));
        saveAsMenuItem.setId("saveAsMenuItem");
        saveAsMenuItem.setDisable(true);
        quitMenuItem = new MenuItem(languageController.translate("menu.quit"));
        quitMenuItem.setId("quitMenuItem");
        fileMenu = new Menu(languageController.translate("menu.file"));
        fileMenu.setId("fileMenu");
        fileMenu.getItems().add(newMenuItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(openMenuItem);
        fileMenu.getItems().add(saveMenuItem);
        fileMenu.getItems().add(saveAsMenuItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(quitMenuItem);

        preferencesMenuItem = new MenuItem(languageController.translate("menu.edit.preferences"));
        preferencesMenuItem.setId("preferencesMenuItem");

        editMenu = new Menu(languageController.translate("menu.edit"));
        editMenu.setId("editMenu");
        editMenu.getItems().add(preferencesMenuItem);

        helpMenuItem = new MenuItem(languageController.translate("menu.help"));
        helpMenuItem.setId("helpMenuItem");
        aboutMenuItem = new MenuItem(languageController.translate("menu.help.about"));
        aboutMenuItem.setId("aboutMenuItem");

        helpMenu = new Menu(languageController.translate("menu.help.help"));
        helpMenu.setId("helpMenu");
        helpMenu.getItems().add(helpMenuItem);
        helpMenu.getItems().add(aboutMenuItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
    }

    @Override
    public Node getNode() {
        return this.menuBar;
    }

    @Override
    public void initialize(EventHandler eventHandler, AbstractModel model) {
        this.menuBarEventHandler = (MenuBarEventHandler) eventHandler;
        this.filesystemModel = (FilesystemModel) model;
        this.createBehaviour();
    }

    private void createBehaviour() {
        this.saveAsMenuItem.setOnAction(event -> menuBarEventHandler.saveAs());
        this.saveMenuItem.setOnAction(event -> menuBarEventHandler.save());
        this.openMenuItem.setOnAction(event -> menuBarEventHandler.open());
        this.newMenuItem.setOnAction(event -> menuBarEventHandler.createFileSystem());
        this.quitMenuItem.setOnAction(event -> menuBarEventHandler.exit());
        this.editMenu.setOnAction(event -> menuBarEventHandler.openPreferencesMenu());
        this.aboutMenuItem.setOnAction(event -> menuBarEventHandler.showAbout());
        this.helpMenuItem.setOnAction(event -> menuBarEventHandler.showHelp());
    }

    @Override
    public void update(String message) {
        boolean isPresent = filesystemModel.isFilesystemInitialized();
        this.saveMenuItem.setDisable(!isPresent);
        this.saveAsMenuItem.setDisable(!isPresent);
    }
}

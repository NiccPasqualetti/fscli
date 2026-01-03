package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.frontend.controller.CommandEventHandler;
import ch.supsi.fscli.frontend.controller.MenuBarEventHandler;
import ch.supsi.fscli.frontend.di.FrontendModule;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.EventManager;
import ch.supsi.fscli.frontend.notification.EventType;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import ch.supsi.fscli.frontend.view.OutputView;
import ch.supsi.fscli.frontend.view.PreferencesMenuView;
import com.google.inject.Guice;
import com.google.inject.Inject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFx extends Application {
    public static final int PREF_INSETS_SIZE = 7;
    private String applicationTitle;

    @Inject
    private LanguageController languageController;
    @Inject
    private FilesystemModel filesystemModel;
    @Inject
    private MenuBarView menuBarView;
    @Inject
    private CommandLineView commandLineView;
    @Inject
    private OutputView outputView;
    @Inject
    private LogView logView;
    @Inject
    private PreferencesMenuView preferencesMenuView;

    @Inject
    private EventManager eventManager;

    @Inject
    private CommandEventHandler filesystemController;
    @Inject
    private MenuBarEventHandler menuBarController;

    @Override
    public void init() {
        Guice.createInjector(new FrontendModule()).injectMembers(this);
        this.applicationTitle = languageController.translate("program.title");

        menuBarView.initialize(menuBarController, filesystemModel);
        commandLineView.initialize(filesystemController, filesystemModel);
        outputView.initialize(filesystemModel);
        logView.initialize(filesystemModel);

        eventManager.subscribe(EventType.NEW_FS, menuBarView);
        eventManager.subscribe(EventType.NEW_FS, commandLineView);
        eventManager.subscribe(EventType.OUTPUT, outputView);
        eventManager.subscribe(EventType.LOG, logView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox top = new VBox(
                menuBarView.getNode(),
                commandLineView.getNode()
        );

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(outputView.getNode());
        rootPane.setBottom(logView.getNode());

        Scene mainScene = new Scene(rootPane);

        primaryStage.setTitle(this.applicationTitle);
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            menuBarController.exit();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}

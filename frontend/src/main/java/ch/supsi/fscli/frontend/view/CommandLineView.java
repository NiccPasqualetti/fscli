package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.controller.CommandEventHandler;
import ch.supsi.fscli.frontend.controller.EventHandler;
import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.frontend.model.AbstractModel;
import ch.supsi.fscli.frontend.model.FilesystemModel;

import ch.supsi.fscli.frontend.notification.Subscriber;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class CommandLineView implements ControlledFxView, Subscriber {
    private static final int PREF_SPACER_WIDTH = 11;

    private CommandEventHandler commandEventHandler;
    private FilesystemModel filesystemModel;
    private final PreferencesController preferencesController;
    private final LanguageController languageController = LanguageController.getInstance();

    private Button enter;
    private Label commandLineLabel;
    private TextField commandLine;
    private HBox commandLinePane;

    public CommandLineView(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
        enter = new Button(languageController.translate("command.enter"));
        enter.setId("enter");
        enter.setDisable(true);

        commandLineLabel = new Label(languageController.translate("command.label"));
        commandLine = new TextField();
        commandLine.setId("commandLine");
        commandLine.setEditable(false);
        commandLine.setFont(Font.font(preferencesController.getCmdLineFont()));
        commandLine.setPrefColumnCount(preferencesController.getCmdLineColumns());

        commandLinePane = new HBox();
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(MainFx.PREF_INSETS_SIZE));

        Region spacer1 = new Region();
        spacer1.setPrefWidth(PREF_SPACER_WIDTH);

        Region spacer2 = new Region();
        spacer2.setPrefWidth(PREF_SPACER_WIDTH);

        commandLinePane.getChildren().add(commandLineLabel);
        commandLinePane.getChildren().add(spacer1);
        commandLinePane.getChildren().add(commandLine);
        commandLinePane.getChildren().add(spacer2);
        commandLinePane.getChildren().add(enter);

        commandLine.setFont(Font.font(preferencesController.getCmdLineFont()));
    }

    @Override
    public Node getNode() {
        return commandLinePane;
    }

    @Override
    public void initialize(EventHandler eventHandler, AbstractModel model) {
        this.commandEventHandler = (CommandEventHandler) eventHandler;
        this.filesystemModel = (FilesystemModel) model;
        this.createBehaviour();
    }

    private void createBehaviour(){
        this.enter.setOnAction(event -> commandEventHandler.executeCommand(commandLine.textProperty().get()));
        this.commandLine.setOnAction(event -> commandEventHandler.executeCommand(commandLine.textProperty().get()));
    }

    @Override
    public void update(String message) {
        boolean isPresent = filesystemModel.isFilesystemInitialized();
        enter.setDisable(!isPresent);
        commandLine.setEditable(isPresent);
    }
}

package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.controller.LanguageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import javafx.scene.layout.HBox;

import java.util.List;
import java.util.stream.IntStream;

public class PreferencesMenuView extends VBox implements DataView {

    private static PreferencesMenuView myself;
    private Stage preferencesStage;
    private Scene preferencesScene;

    private ComboBox<String> languageComboBox;
    private ComboBox<Integer> logAreaLines;
    private ComboBox<Integer> outputAreaLines;
    private ComboBox<Integer> cmdLineColumns;
    private ComboBox<String> logAreaFont;
    private ComboBox<String> outputAreaFont;
    private ComboBox<String> cmdLineFont;

    private Button saveButton;
    private Button cancelButton;
    private boolean hasUnsavedChanges = false;

    private final PreferencesController preferencesController;
    LanguageController languageController = LanguageController.getInstance();

    public static final int MIN_LINES = 3;
    public static final int MAX_LINES = 20;
    public static final int MIN_COLUMNS = 10;
    public static final int MAX_COLUMNS = 100;

    private PreferencesMenuView(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
        init();
    }

    private void setupChangeListeners() {
        languageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        logAreaLines.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        outputAreaLines.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        cmdLineColumns.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        logAreaFont.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        outputAreaFont.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
        cmdLineFont.valueProperty().addListener((obs, oldVal, newVal) -> markUnsavedChanges());
    }

    private void markUnsavedChanges() {
        this.hasUnsavedChanges = true;
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        if (hasUnsavedChanges) {
            saveButton.setStyle("-fx-border-color: #ff3333; -fx-border-width: 2px;");
        } else {
            saveButton.setStyle("");
        }
    }

    public void markAsSaved() {
        this.hasUnsavedChanges = false;
        updateSaveButtonState();
    }

    public void init (){
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);

        Label languageLabel = new Label(languageController.translate("preferences.language"));
        languageComboBox = new ComboBox<>();
        languageComboBox.setItems(FXCollections.observableArrayList(languageController.getSupportedLanguages()));
        languageComboBox.setValue(languageController.getCurrentLanguage());

        ObservableList<Integer> lineNumbers = FXCollections.observableArrayList(IntStream.range(MIN_LINES,MAX_LINES+1).boxed().toList());
        ObservableList<Integer> columnNumbers = FXCollections.observableArrayList(IntStream.range(MIN_COLUMNS,MAX_COLUMNS+1).boxed().toList());

        Label logAreaLinesLabel = new Label(languageController.translate("preferences.logAreaLines"));
        logAreaLines = new ComboBox<>(lineNumbers);
        logAreaLines.setValue(preferencesController.getLogAreaLines());

        Label outputAreaLinesLabel = new Label(languageController.translate("preferences.outputAreaLines"));
        outputAreaLines = new ComboBox<>(lineNumbers);
        outputAreaLines.setValue(preferencesController.getOutputAreaLines());

        Label cmdLineColumnsLabel = new Label(languageController.translate("preferences.cmdLineColumns"));
        cmdLineColumns = new ComboBox<>(columnNumbers);
        cmdLineColumns.setValue(preferencesController.getCmdLineColumns());

        Label logAreaFontLabel = new Label(languageController.translate("preferences.logAreaFont"));
        Label outputAreaFontLabel = new Label(languageController.translate("preferences.outputAreaFont"));
        Label cmdLineFontLabel = new Label(languageController.translate("preferences.cmdLineFont"));

        List<String> allFonts = Font.getFamilies();
        logAreaFont = new ComboBox<>(FXCollections.observableArrayList(allFonts));
        logAreaFont.setValue(Font.font(preferencesController.getLogAreaFont()).getFamily());
        outputAreaFont = new ComboBox<>(FXCollections.observableArrayList(allFonts));
        outputAreaFont.setValue(Font.font(preferencesController.getOutputAreaFont()).getFamily());
        cmdLineFont = new ComboBox<>(FXCollections.observableArrayList(allFonts));
        cmdLineFont.setValue(Font.font(preferencesController.getCmdLineFont()).getFamily());

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        saveButton = new Button(languageController.translate("preferences.save"));
        cancelButton = new Button(languageController.translate("preferences.cancel"));

        buttonContainer.getChildren().addAll(saveButton, cancelButton);

        this.getChildren().addAll(
                languageLabel, languageComboBox,
                cmdLineColumnsLabel, cmdLineColumns,
                outputAreaLinesLabel, outputAreaLines,
                logAreaLinesLabel, logAreaLines,
                cmdLineFontLabel, cmdLineFont,
                outputAreaFontLabel, outputAreaFont,
                logAreaFontLabel, logAreaFont,
                buttonContainer
        );

        setupChangeListeners();

    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Stage getStage() {
        if (preferencesStage == null) {
            preferencesStage = new Stage();
            preferencesStage.setTitle(languageController.translate("preferences.title"));
            preferencesStage.setOnCloseRequest(event -> {
                event.consume();
                preferencesStage.hide();
            });
            preferencesScene = new Scene(this,
                    Screen.getPrimary().getBounds().getWidth() * (25./96.),
                    Screen.getPrimary().getBounds().getHeight() * (65./108.)
            );
            preferencesStage.setScene(preferencesScene);
        }
        return preferencesStage;
    }

    public void setSelectedLanguage(String language) {
        languageComboBox.setValue(language);
    }

    public String getSelectedLanguage() {
        return languageComboBox.getValue();
    }

    public void setLogAreaLines(int lines) {
        logAreaLines.setValue(lines);
    }

    public int getLogAreaLines() {
        return logAreaLines.getValue();
    }

    public void setOutputAreaLines(int lines) {
        outputAreaLines.setValue(lines);
    }

    public int getOutputAreaLines() {
        return outputAreaLines.getValue();
    }

    public void setCmdLineColumns(int columns) {
        cmdLineColumns.setValue(columns);
    }

    public int getCmdLineColumns() {
        return cmdLineColumns.getValue();
    }

    public void setLogAreaFont(String font) {
        logAreaFont.setValue(font);
    }

    public String getLogAreaFont() {
        return logAreaFont.getValue();
    }

    public void setOutputAreaFont(String font) {
        outputAreaFont.setValue(font);
    }

    public String getOutputAreaFont() {
        return outputAreaFont.getValue();
    }

    public void setCmdLineFont(String font) {
        cmdLineFont.setValue(font);
    }

    public String getCmdLineFont() {
        return cmdLineFont.getValue();
    }

    public static PreferencesMenuView getInstance(PreferencesController preferencesController) {
        if (myself == null) {
            myself = new PreferencesMenuView(preferencesController);
        }
        return myself;
    }
}

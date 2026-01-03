package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.frontend.model.AbstractModel;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.Subscriber;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class LogView implements UncontrolledFxView, Subscriber {
    private FilesystemModel filesystemModel;
    private final PreferencesController preferencesController;
    private final LanguageController languageController = LanguageController.getInstance();

    private TextArea logView;
    private ScrollPane bottomPane;

    public LogView(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
        logView = new TextArea();
        logView.setId("logView");
        logView.setFont(Font.font(preferencesController.getLogAreaFont()));
        logView.setPrefRowCount(preferencesController.getLogAreaLines());
        logView.setEditable(false);

        bottomPane = new ScrollPane();
        bottomPane.setFitToHeight(true);
        bottomPane.setFitToWidth(true);
        bottomPane.setPadding(new Insets(MainFx.PREF_INSETS_SIZE));
        bottomPane.setContent(logView);
    }

    @Override
    public Node getNode() {
        return bottomPane;
    }

    @Override
    public void initialize(AbstractModel model) {
        this.filesystemModel = (FilesystemModel) model;
    }

    @Override
    public void update(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String oraCorrente = LocalTime.now().format(formatter);

        String log = this.filesystemModel.getFeedback();
        if (log != null && !log.isEmpty()) {
            int maxLines = preferencesController.getLogAreaLines();
            if (logView.getText().lines().count() >= maxLines) {
                String currentText = logView.getText();
                logView.clear();
                String newText = currentText.lines().skip(1).limit(maxLines-1).collect(Collectors.joining("\n"));
                logView.setText(newText);
                logView.appendText("\n");
            }
            logView.appendText(oraCorrente + " --> " + log);
            logView.appendText("\n");
        }else if(message != null){
            String msgTranslated = languageController.translate(message);
            this.logView.setText(oraCorrente + " --> " + msgTranslated + "\n");
        }
    }
}

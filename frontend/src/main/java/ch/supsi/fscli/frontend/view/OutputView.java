package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.model.AbstractModel;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.Subscriber;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.util.stream.Collectors;

public class OutputView implements UncontrolledFxView, Subscriber {
    private FilesystemModel filesystemModel;
    private final PreferencesController preferencesController;

    private TextArea outputView;
    private ScrollPane centerPane;

    public OutputView(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
        outputView = new TextArea();
        outputView.setId("outputView");
        outputView.setFont(Font.font(preferencesController.getOutputAreaFont()));
        outputView.setPrefRowCount(preferencesController.getOutputAreaLines());
        outputView.setEditable(false);

        centerPane = new ScrollPane();
        centerPane.setFitToHeight(true);
        centerPane.setFitToWidth(true);
        centerPane.setPadding(new Insets(MainFx.PREF_INSETS_SIZE));
        centerPane.setContent(outputView);
    }

    @Override
    public Node getNode() {
        return centerPane;
    }

    @Override
    public void initialize(AbstractModel model) {
        this.filesystemModel = (FilesystemModel) model;
    }

    @Override
    public void update(String message) {
        if (filesystemModel.isFilesystemInitialized()) {
            if (filesystemModel.needToClearOutput()) {
                this.outputView.clear();
            } else {
                String output = filesystemModel.getTempCommandOutput();
                if (output != null && !output.isEmpty()) {
                    int maxLines = preferencesController.getOutputAreaLines();

                    if (outputView.getText().lines().count() >= maxLines) {
                        String currentText = outputView.getText();
                        outputView.clear();
                        String newText = currentText.lines().skip(1).limit(maxLines - 1).collect(Collectors.joining("\n"));
                        outputView.setText(newText);
                        outputView.appendText("\n");
                    }
                    outputView.appendText(output);
                    outputView.appendText("\n");
                }
            }
        }
    }
}
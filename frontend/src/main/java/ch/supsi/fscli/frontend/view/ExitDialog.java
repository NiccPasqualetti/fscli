package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.LanguageController;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ExitDialog extends Dialog<Boolean> {

    public ExitDialog() {
        super();
        LanguageController languageController = LanguageController.getInstance();
        String title = languageController.translate("exit.title");
        String header = languageController.translate("exit.header");
        String label = languageController.translate("exit.label");
        String btnYes = languageController.translate("exit.btn.yes");
        String btnNo = languageController.translate("exit.btn.no");

        setTitle(title);
        setHeaderText(header);

        VBox content = new VBox(10);
        content.getChildren().add(new Label(label));
        getDialogPane().setContent(content);

        ButtonType yesButton = new ButtonType(btnYes, ButtonData.YES);
        ButtonType noButton = new ButtonType(btnNo, ButtonData.NO);
        getDialogPane().getButtonTypes().addAll(yesButton, noButton);

        setResultConverter(dialogButton -> dialogButton == yesButton);
    }
}

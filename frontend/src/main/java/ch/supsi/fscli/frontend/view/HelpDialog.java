package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.controller.LanguageController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HelpDialog extends DialogPane {

    private final LanguageController languageController;

    private HelpDialog(LanguageController languageController) {
        super();
        this.languageController = languageController;
        buildContent();
    }

    private void buildContent() {
        setHeaderText(languageController.translate("help.dialog.title"));

        Accordion accordion = new Accordion();
        accordion.setPadding(new Insets(6));
        accordion.getPanes().addAll(
                titledPane(languageController.translate("help.overview.title"), languageController.translate("help.overview.text")),
                titledPane(languageController.translate("help.files.title"), languageController.translate("help.files.text")),
                titledPane(languageController.translate("help.commands.title"), languageController.translate("help.commands.text")),
                titledPane(languageController.translate("help.outputlog.title"), languageController.translate("help.outputlog.text")),
                titledPane(languageController.translate("help.feedback.title"), languageController.translate("help.feedback.text")),
                titledPane(languageController.translate("help.preferences.title"), languageController.translate("help.preferences.text")),
                titledPane(languageController.translate("help.limits.title"), languageController.translate("help.limits.text"))
        );

        if (!accordion.getPanes().isEmpty()) {
            accordion.setExpandedPane(accordion.getPanes().get(0));
        }

        VBox container = new VBox(8);
        container.setPadding(new Insets(10));
        container.getChildren().add(accordion);
        container.setPrefWidth(680);
        container.setMinHeight(Region.USE_PREF_SIZE);

        setContent(container);

        ButtonType ok = ButtonType.OK;
        getButtonTypes().setAll(ok);
    }

    private TitledPane titledPane(String title, String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setMaxWidth(640);
        TitledPane tp = new TitledPane(title, lbl);
        tp.setCollapsible(true);
        tp.setAnimated(true);
        return tp;
    }

    public static void show(Window owner) {
        LanguageController languageController = LanguageController.getInstance();
        Dialog<Void> dialog = new Dialog<>();
        HelpDialog pane = new HelpDialog(languageController);
        dialog.setDialogPane(pane);

        if (owner instanceof Stage) {
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            ((Stage) owner).getIcons().stream().findFirst().ifPresent(icon -> ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(icon));
        }

        dialog.setTitle(languageController.translate("help.dialog.name"));
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(720, 520);
        dialog.showAndWait();
    }
}

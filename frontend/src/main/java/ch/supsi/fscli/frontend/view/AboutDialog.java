package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.backend.controller.LanguageController;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AboutDialog extends Stage {
    private static final String UNKNOWN_VALUE = "unknown";
    private static final String[] AUTHORS = {
            "Niccolò Pasqualetti",
            "Daniele Cereghetti",
            "Sebastiano Piubellini"
    };

    public AboutDialog() {
        LanguageController languageController = LanguageController.getInstance();
        String appTitle = languageController.translate("program.title");
        String version = resolveVersion();
        String buildDate = resolveBuildDate();

        setTitle(languageController.translate("menu.help.about"));
        initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label(appTitle);
        Label orgLabel = new Label(languageController.translate("about.developed"));
        Label authorsLabel = new Label(languageController.translate("about.authors") + ": " + String.join(", ", AUTHORS));
        Label versionLabel = new Label(languageController.translate("about.version") + ": " + version);
        Label buildDateLabel = new Label(languageController.translate("about.buildDate") + ": " + buildDate);

        VBox layout = new VBox(8, titleLabel, orgLabel, authorsLabel, versionLabel, buildDateLabel);
        layout.setPadding(new Insets(12));

        Scene scene = new Scene(layout);
        setScene(scene);
        setResizable(false);
    }

    private String resolveVersion() {
        Package pkg = MainFx.class.getPackage();
        if (pkg != null) {
            String implementationVersion = pkg.getImplementationVersion();
            if (implementationVersion != null && !implementationVersion.isBlank()) {
                return implementationVersion;
            }
        }
        return UNKNOWN_VALUE;
    }

    private String resolveBuildDate() {
        String buildTime = readManifestAttribute("Build-Time");
        if (buildTime != null && !buildTime.isBlank()) {
            return buildTime;
        }
        return UNKNOWN_VALUE;
    }

    private String readManifestAttribute(String attributeName) {
        try {
            Enumeration<URL> resources = MainFx.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL manifestUrl = resources.nextElement();
                try (InputStream inputStream = manifestUrl.openStream()) {
                    Manifest manifest = new Manifest(inputStream);
                    Attributes attributes = manifest.getMainAttributes();
                    String value = attributes.getValue(attributeName);
                    if (value != null && !value.isBlank()) {
                        return value;
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}

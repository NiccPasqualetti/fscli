package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.PreferencesBusinessInterface;
import ch.supsi.fscli.backend.service.PreferencesModel;

/**
 * Frontend-facing controller wrapping preferences operations so the UI
 * does not depend on backend service classes.
 */
public class PreferencesController {
    private final PreferencesBusinessInterface preferences;

    public PreferencesController() {
        this(PreferencesModel.getInstance());
    }

    PreferencesController(PreferencesBusinessInterface preferences) {
        this.preferences = preferences;
    }

    public String getCurrentLanguage() {
        return preferences.getCurrentLanguage();
    }

    public String getLogAreaFont() {
        return preferences.getLogAreaFont();
    }

    public String getOutputAreaFont() {
        return preferences.getOutputAreaFont();
    }

    public String getCmdLineFont() {
        return preferences.getCmdLineFont();
    }

    public int getLogAreaLines() {
        return preferences.getLogAreaLines();
    }

    public int getOutputAreaLines() {
        return preferences.getOutputAreaLines();
    }

    public int getCmdLineColumns() {
        return preferences.getCmdLineColumns();
    }

    public void savePreferences(String language, String logAreaFont,
                                String outputAreaFont, String cmdLineFont,
                                int logAreaLines, int outputAreaLines, int cmdLineColumns) {
        preferences.savePreferences(language, logAreaFont, outputAreaFont, cmdLineFont,
                logAreaLines, outputAreaLines, cmdLineColumns);
    }
}

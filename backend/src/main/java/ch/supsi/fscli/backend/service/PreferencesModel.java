package ch.supsi.fscli.backend.service;

import java.util.HashMap;
import java.util.Map;

import ch.supsi.fscli.backend.repository.PropertiesDataAccess;
import ch.supsi.fscli.backend.repository.PropertiesDataAccessInterface;

public class PreferencesModel implements PreferencesBusinessInterface {

    private static PreferencesModel myself;

    private final PropertiesDataAccessInterface propertiesDataAccess;

    private final Map<String, String> userPreferences;

    private PreferencesModel() {
        propertiesDataAccess = PropertiesDataAccess.getInstance();
        userPreferences = propertiesDataAccess.getUserPreferences();
    }

    public static PreferencesModel getInstance() {
        if (myself == null) {
            myself = new PreferencesModel();
        }
        return myself;
    }

    @Override
    public String getCurrentLanguage() {
        return userPreferences.get("language");
    }

    @Override
    public String getLogAreaFont() {
        return userPreferences.get("logAreaFont");
    }

    @Override
    public String getOutputAreaFont() {
        return userPreferences.get("outputAreaFont");
    }

    @Override
    public String getCmdLineFont() {
        return userPreferences.get("cmdLineFont");
    }

    @Override
    public int getLogAreaLines() {
        return Integer.parseInt(userPreferences.get("logAreaLines"));
    }

    @Override
    public int getOutputAreaLines() {
        return Integer.parseInt(userPreferences.get("outputAreaLines"));
    }

    @Override
    public int getCmdLineColumns() {
        return Integer.parseInt(userPreferences.get("cmdLineColumns"));
    }

    @Override
    public void savePreferences(String language, String logAreaFont,
                                String outputAreaFont, String cmdLineFont,
                                int logAreaLines, int outputAreaLines, int cmdLineColumns) {
        Map<String, String> preferences = new HashMap<>();

        preferences.put("language", language);
        preferences.put("logAreaFont", logAreaFont);
        preferences.put("outputAreaFont", outputAreaFont);
        preferences.put("cmdLineFont", cmdLineFont);
        preferences.put("logAreaLines", String.valueOf(logAreaLines));
        preferences.put("outputAreaLines", String.valueOf(outputAreaLines));
        preferences.put("cmdLineColumns", String.valueOf(cmdLineColumns));

        propertiesDataAccess.save(preferences);
    }
}

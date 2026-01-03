package ch.supsi.fscli.backend.service;

public interface PreferencesBusinessInterface {

    String getCurrentLanguage();

    String getLogAreaFont();

    String getOutputAreaFont();

    String getCmdLineFont();

    int getLogAreaLines();

    int getOutputAreaLines();

    int getCmdLineColumns();

    void savePreferences(String language, String logAreaFont,
                         String outputAreaFont, String cmdLineFont,
                         int logAreaLines, int outputAreaLines, int cmdLineColumns);

}

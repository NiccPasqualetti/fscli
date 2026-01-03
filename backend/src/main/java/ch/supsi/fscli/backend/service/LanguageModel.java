package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.repository.LanguageDataAccess;
import ch.supsi.fscli.backend.repository.LanguageDataAccessInterface;
import ch.supsi.fscli.backend.repository.PropertiesDataAccess;
import ch.supsi.fscli.backend.repository.PropertiesDataAccessInterface;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LanguageModel implements LanguageBusinessInterface {

    private static LanguageModel myself;

    private final LanguageDataAccessInterface languageDataAccess;

    private final PropertiesDataAccessInterface propertiesDataAccess;

    private final Map<String, String> userPreferences;

    private final String language;

    private final Locale locale;

    private final Map<String, String> translations;

    private LanguageModel() {

        propertiesDataAccess = PropertiesDataAccess.getInstance();

        userPreferences = propertiesDataAccess.getUserPreferences();

        languageDataAccess = LanguageDataAccess.getInstance();

        language = userPreferences.get("language");

        locale = Locale.forLanguageTag(language);

        languageDataAccess.init(locale);

        translations = languageDataAccess.getTranslations();

    }

    public static LanguageModel getInstance() {
        if (myself == null) {
            myself = new LanguageModel();
        }
        return myself;
    }

    @Override
    public String translate(String key) {
        return translations.get(key);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return languageDataAccess.getResourceBundle();
    }

    @Override
    public String getCurrentLanguage() {
        return language;
    }

    @Override
    public Locale getCurrentLocale() {
        return locale;
    }

    @Override
    public List<String> getSupportedLanguages() {
        return languageDataAccess.getSupportedLanguages();
    }
}
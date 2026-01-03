package ch.supsi.fscli.backend.service;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface LanguageBusinessInterface {

    String translate(String key);

    String getCurrentLanguage();

    Locale getCurrentLocale();

    ResourceBundle getResourceBundle();

    List<String> getSupportedLanguages();
}
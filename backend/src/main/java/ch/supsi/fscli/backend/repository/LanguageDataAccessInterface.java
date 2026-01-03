package ch.supsi.fscli.backend.repository;

import java.util.*;

public interface LanguageDataAccessInterface {

    Map<String, String> getTranslations();

    ResourceBundle getResourceBundle();

    List<String> getSupportedLanguages();

    void init(Locale locale);

}
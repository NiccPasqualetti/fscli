package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.LanguageBusinessInterface;
import ch.supsi.fscli.backend.service.LanguageModel;

import java.util.List;

/**
 * Controller wrapper around language business logic to keep frontend
 * decoupled from backend services.
 */
public class LanguageController {

    private static LanguageController myself;
    private final LanguageBusinessInterface languageBusiness;

    private LanguageController() {
        languageBusiness = LanguageModel.getInstance();
    }

    public static LanguageController getInstance() {
        if (myself == null) {
            myself = new LanguageController();
        }
        return myself;
    }

    public String translate(String key) {
        return languageBusiness.translate(key);
    }

    public String getCurrentLanguage() {
        return languageBusiness.getCurrentLanguage();
    }

    public List<String> getSupportedLanguages() {
        return languageBusiness.getSupportedLanguages();
    }
}

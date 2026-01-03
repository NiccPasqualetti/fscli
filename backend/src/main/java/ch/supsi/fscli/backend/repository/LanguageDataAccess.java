package ch.supsi.fscli.backend.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LanguageDataAccess implements LanguageDataAccessInterface {

    private static LanguageDataAccess myself;

    private ResourceBundle backendBundle;
    private ResourceBundle frontendBundle;
    private Map<String, String> mergedTranslations = Map.of();

    private static final String SUPPORTED_LANGS_PATH = "/properties/supportedLanguages.properties";

    private LanguageDataAccess() {}

    @Override
    public void init(Locale locale) {
        backendBundle = ResourceBundle.getBundle("i18n.backend_labels", locale);
        try {
            frontendBundle = ResourceBundle.getBundle("i18n.labels", locale);
        } catch (MissingResourceException ignored) {
            frontendBundle = null;
        }
        mergedTranslations = buildMergedTranslations();
    }

    public static LanguageDataAccess getInstance() {
        if (myself == null) {
            myself = new LanguageDataAccess();
        }
        return myself;
    }

    public ResourceBundle getResourceBundle() {
        return new MapResourceBundle(mergedTranslations);
    }

    @Override
    public List<String> getSupportedLanguages() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream(SUPPORTED_LANGS_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.values().stream()
                .map(Object::toString)
                .toList();
    }

    @Override
    public Map<String, String> getTranslations() {

        Map<String, String> translations = new HashMap<>();

        return new HashMap<>(mergedTranslations);
    }

    private Map<String, String> buildMergedTranslations() {
        Map<String, String> translations = new HashMap<>();
        for (String key : backendBundle.keySet()) {
            translations.put(key, backendBundle.getString(key));
        }
        if (frontendBundle != null) {
            for (String key : frontendBundle.keySet()) {
                translations.put(key, frontendBundle.getString(key));
            }
        }
        return translations;
    }

    private static class MapResourceBundle extends ResourceBundle {
        private final Map<String, String> data;

        MapResourceBundle(Map<String, String> data) {
            this.data = data;
        }

        @Override
        protected Object handleGetObject(String key) {
            return data.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(data.keySet());
        }
    }
}

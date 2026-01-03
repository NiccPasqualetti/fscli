package ch.supsi.fscli.backend.repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class PropertiesDataAccess implements PropertiesDataAccessInterface {

    private static final String APP_NAME = "filesystem_simulator";

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final String DEFAULT_CONFIG_PATH = "/properties/defaultPrefs.properties";

    private static final String OS = System.getProperty("os.name");

    private static final Path CONFIG_FILE_PATH = OS.toLowerCase().contains("win")
            ? Paths.get(System.getenv("APPDATA"), APP_NAME, CONFIG_FILE_NAME)
            : Paths.get(System.getProperty("user.home"), ".config", APP_NAME, CONFIG_FILE_NAME);

    private static Properties properties;

    private static PropertiesDataAccess propertiesDataAccess;

    private PropertiesDataAccess() {}

    public static PropertiesDataAccess getInstance() {
        if (propertiesDataAccess == null) {
            propertiesDataAccess = new PropertiesDataAccess();
        }
        return propertiesDataAccess;
    }

    @Override
    public void save(Map<String, String> settings) {
        Properties newProperties = new Properties();
        newProperties.putAll(settings);

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH.toString())) {
            newProperties.store(output, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean configExists() {
        return Files.isRegularFile(CONFIG_FILE_PATH);
    }

    @Override
    public Properties getConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH.toString())) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Error trying to load properties file: " + e.getMessage());
        }
        return properties;
    }

    @Override
    public void createDefaultConfig(Properties defaultProperties) {
        if (!Files.isDirectory(CONFIG_FILE_PATH.getParent())) {
            try {
                Files.createDirectories(CONFIG_FILE_PATH.getParent());
            } catch (IOException e) {
                System.err.println("Error trying to create config directory: " + e.getMessage());
            }
        }

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH.toString())) {
            defaultProperties.store(output, "Default configuration");
        } catch (IOException e) {
            System.err.println("Error trying to create config.properties file:" + e.getMessage());
        }
    }

    private Properties loadDefault() {
        Properties defaultPreferences = new Properties();
        try {
            InputStream defaultPreferencesStream = this.getClass().getResourceAsStream(DEFAULT_CONFIG_PATH);
            defaultPreferences.load(defaultPreferencesStream);
        } catch (IOException e) {
            System.err.println("Error loading default preferences:" + e.getMessage());
        }
        return defaultPreferences;
    }

    @Override
    public Map<String, String> getUserPreferences() {
        if (properties != null) {
            return ConvertToMap.run(properties);
        }

        if (!configExists()) {
            Properties defaultProperties = loadDefault();
            createDefaultConfig(defaultProperties);
        }
        properties = getConfig();

        return ConvertToMap.run(properties);
    }
}
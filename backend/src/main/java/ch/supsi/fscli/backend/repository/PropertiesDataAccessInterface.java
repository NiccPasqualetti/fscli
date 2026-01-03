package ch.supsi.fscli.backend.repository;

import java.util.Map;
import java.util.Properties;

public interface PropertiesDataAccessInterface {

    boolean configExists();

    Properties getConfig();

    void createDefaultConfig(Properties defaultProperties);

    Map<String, String> getUserPreferences();

    void save(Map<String, String> settings);
}
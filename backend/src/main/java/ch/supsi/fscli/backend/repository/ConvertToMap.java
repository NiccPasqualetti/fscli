package ch.supsi.fscli.backend.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConvertToMap {
    public static Map<String, String> run(Properties properties) {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }
}

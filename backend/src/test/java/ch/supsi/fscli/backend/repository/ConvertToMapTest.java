package ch.supsi.fscli.backend.repository;

import org.junit.jupiter.api.Test;
import java.util.Properties;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertToMapTest {

    @Test
    void testMap() {
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        properties.setProperty("key3", "value3");

        Map<String, String> result = ConvertToMap.run(properties);

        assertEquals(3, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
        assertEquals("value3", result.get("key3"));
    }

    @Test
    void testEmptyMap() {
        Properties properties = new Properties();

        Map<String, String> result = ConvertToMap.run(properties);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
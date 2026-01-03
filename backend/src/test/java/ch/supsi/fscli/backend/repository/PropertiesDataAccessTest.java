package ch.supsi.fscli.backend.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesDataAccessTest {

    private PropertiesDataAccess dataAccess;
    private Path configPath;

    private byte[] originalConfigContent;
    private boolean originalConfigExisted = false;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = PropertiesDataAccess.class.getDeclaredField("propertiesDataAccess");
        instance.setAccessible(true);
        instance.set(null, null);

        dataAccess = PropertiesDataAccess.getInstance();

        Field pathField = PropertiesDataAccess.class.getDeclaredField("CONFIG_FILE_PATH");
        pathField.setAccessible(true);
        configPath = (Path) pathField.get(null);

        if (Files.exists(configPath)) {
            originalConfigExisted = true;
            originalConfigContent = Files.readAllBytes(configPath);
            Files.delete(configPath);
        }

        if (!Files.exists(configPath.getParent())) {
            Files.createDirectories(configPath.getParent());
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (originalConfigExisted) {
            if (!Files.exists(configPath.getParent())) {
                Files.createDirectories(configPath.getParent());
            }
            Files.write(configPath, originalConfigContent);
        } else {
            Files.deleteIfExists(configPath);
        }
    }

    @Test
    void testSingletonInstance() {
        PropertiesDataAccess instance1 = PropertiesDataAccess.getInstance();
        PropertiesDataAccess instance2 = PropertiesDataAccess.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "getInstance dovrebbe restituire sempre lo stesso oggetto");
    }

    @Test
    void testConfigExists() throws IOException {
        assertFalse(dataAccess.configExists(), "Il file di configurazione non dovrebbe esistere all'inizio");

        Files.createFile(configPath);

        assertTrue(dataAccess.configExists(), "Il metodo dovrebbe ritornare true se il file esiste");
    }

    @Test
    void testCreateDefaultConfig() {
        Properties defaults = new Properties();
        defaults.setProperty("theme", "dark");
        defaults.setProperty("language", "en");

        dataAccess.createDefaultConfig(defaults);

        assertTrue(Files.exists(configPath), "Il file di configurazione dovrebbe essere stato creato");

        Properties loaded = new Properties();
        try (var stream = Files.newInputStream(configPath)) {
            loaded.load(stream);
        } catch (IOException e) {
            fail("Impossibile leggere il file creato");
        }

        assertEquals("dark", loaded.getProperty("theme"));
        assertEquals("en", loaded.getProperty("language"));
    }

    @Test
    void testSaveAndGetConfig() {
        Map<String, String> settings = new HashMap<>();
        settings.put("autosave", "true");
        settings.put("max_files", "100");

        assertDoesNotThrow(() -> dataAccess.save(settings));
        assertTrue(Files.exists(configPath));
        Properties loadedProps = dataAccess.getConfig();
        assertNotNull(loadedProps);
        assertEquals("true", loadedProps.getProperty("autosave"));
        assertEquals("100", loadedProps.getProperty("max_files"));
    }

    @Test
    void testGetUserPreferencesFlow() {
        assertFalse(dataAccess.configExists());

        Map<String, String> prefs = null;
        try {
            prefs = dataAccess.getUserPreferences();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertTrue(dataAccess.configExists(), "getUserPreferences dovrebbe creare il file se manca");
        assertNotNull(prefs);
    }
}

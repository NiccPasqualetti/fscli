package ch.supsi.fscli.backend.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LanguageDataAccessTest {

    private LanguageDataAccess dataAccess;

    private Path supportedLangsPath;
    private Path labelsPath;

    private byte[] originalSupportedBytes;
    private boolean supportedExisted = false;

    private byte[] originalLabelsBytes;
    private boolean labelsExisted = false;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = LanguageDataAccess.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);

        ResourceBundle.clearCache();

        dataAccess = LanguageDataAccess.getInstance();

        Path classpathRoot;
        try {
            classpathRoot = Paths.get(Objects.requireNonNull(getClass().getResource("/")).toURI());
        } catch (NullPointerException | URISyntaxException e) {
            classpathRoot = Paths.get(System.getProperty("user.dir"));
        }

        supportedLangsPath = classpathRoot.resolve("properties/supportedLanguages.properties");
        labelsPath = classpathRoot.resolve("i18n/en_US.properties");

        if (Files.exists(supportedLangsPath)) {
            supportedExisted = true;
            originalSupportedBytes = Files.readAllBytes(supportedLangsPath);
        }

        if (Files.exists(labelsPath)) {
            labelsExisted = true;
            originalLabelsBytes = Files.readAllBytes(labelsPath);
        }

        Files.createDirectories(supportedLangsPath.getParent());
        Files.createDirectories(labelsPath.getParent());

        String supportedContent = "lang.en=English\nlang.it=Italian\n";
        Files.write(supportedLangsPath, supportedContent.getBytes());

        String labelsContent = "greeting=Hello\nfarewell=Goodbye\n";
        Files.write(labelsPath, labelsContent.getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        ResourceBundle.clearCache();

        if (supportedExisted) {
            Files.write(supportedLangsPath, originalSupportedBytes);
        } else {
            Files.deleteIfExists(supportedLangsPath);
            try { Files.deleteIfExists(supportedLangsPath.getParent()); } catch (Exception ignore) {}
        }

        if (labelsExisted) {
            Files.write(labelsPath, originalLabelsBytes);
        } else {
            Files.deleteIfExists(labelsPath);
            try { Files.deleteIfExists(labelsPath.getParent()); } catch (Exception ignore) {}
        }

        try {
            Field instance = LanguageDataAccess.class.getDeclaredField("myself");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (ReflectiveOperationException ignored) {}
    }

    @Test
    void testGetSupportedLanguages() {
        List<String> langs = dataAccess.getSupportedLanguages();

        assertNotNull(langs);
        assertTrue(langs.contains("English"), "Supported languages should contain English");
        assertTrue(langs.contains("Italian"), "Supported languages should contain Italian");
    }

    @Disabled
    void testInitAndGetTranslations() {
        dataAccess.init(Locale.ENGLISH);
        assertNotNull(dataAccess.getResourceBundle(), "ResourceBundle should be initialized after init()");

        Map<String, String> translations = dataAccess.getTranslations();
        assertNotNull(translations);
        assertEquals("Hello", translations.get("greeting"));
        assertEquals("Goodbye", translations.get("farewell"));
    }
}

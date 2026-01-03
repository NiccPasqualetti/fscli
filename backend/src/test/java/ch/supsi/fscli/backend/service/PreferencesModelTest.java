package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.repository.PropertiesDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesModelTest {

    @BeforeEach
    void setUp() throws Exception {
        Field instance = PreferencesModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field instance = PreferencesModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testGettersAndSave_withMockedDataAccess() {
        PropertiesDataAccess mockDataAccess = mock(PropertiesDataAccess.class);

        Map<String, String> prefs = new HashMap<>();
        prefs.put("language", "it");
        prefs.put("logAreaFont", "Monaco-12");
        prefs.put("outputAreaFont", "Consolas-11");
        prefs.put("cmdLineFont", "Courier-10");
        prefs.put("logAreaLines", "50");
        prefs.put("outputAreaLines", "30");
        prefs.put("cmdLineColumns", "120");

        when(mockDataAccess.getUserPreferences()).thenReturn(prefs);

        try (MockedStatic<PropertiesDataAccess> mockedStatic = mockStatic(PropertiesDataAccess.class)) {
            mockedStatic.when(PropertiesDataAccess::getInstance).thenReturn(mockDataAccess);

            PreferencesModel model = PreferencesModel.getInstance();

            assertEquals("it", model.getCurrentLanguage());
            assertEquals("Monaco-12", model.getLogAreaFont());
            assertEquals("Consolas-11", model.getOutputAreaFont());
            assertEquals("Courier-10", model.getCmdLineFont());
            assertEquals(50, model.getLogAreaLines());
            assertEquals(30, model.getOutputAreaLines());
            assertEquals(120, model.getCmdLineColumns());

            model.savePreferences("en", "A", "B", "C", 10, 20, 40);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
            verify(mockDataAccess, times(1)).save(captor.capture());

            Map<String, String> saved = captor.getValue();
            assertEquals("en", saved.get("language"));
            assertEquals("A", saved.get("logAreaFont"));
            assertEquals("B", saved.get("outputAreaFont"));
            assertEquals("C", saved.get("cmdLineFont"));
            assertEquals("10", saved.get("logAreaLines"));
            assertEquals("20", saved.get("outputAreaLines"));
            assertEquals("40", saved.get("cmdLineColumns"));
        }
    }
}

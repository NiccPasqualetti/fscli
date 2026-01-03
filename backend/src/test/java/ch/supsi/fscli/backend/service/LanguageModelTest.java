package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.repository.LanguageDataAccess;
import ch.supsi.fscli.backend.repository.PropertiesDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageModelTest {

    @BeforeEach
    void setUp() throws Exception {
        Field instance = LanguageModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field instance = LanguageModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testTranslateAndGetters_withMockedDataAccesses() throws Exception {
        PropertiesDataAccess mockProps = mock(PropertiesDataAccess.class);
        LanguageDataAccess mockLang = mock(LanguageDataAccess.class);

        Map<String, String> prefs = Map.of("language", "en");
        when(mockProps.getUserPreferences()).thenReturn(prefs);

        Map<String, String> translations = Map.of("greeting", "Hello", "bye", "Bye");
        when(mockLang.getTranslations()).thenReturn(translations);

        ResourceBundle fakeBundle = mock(ResourceBundle.class);
        when(mockLang.getResourceBundle()).thenReturn(fakeBundle);

        List<String> supported = List.of("English", "Italian");
        when(mockLang.getSupportedLanguages()).thenReturn(supported);

        try (MockedStatic<PropertiesDataAccess> propsStatic = mockStatic(PropertiesDataAccess.class);
             MockedStatic<LanguageDataAccess> langStatic = mockStatic(LanguageDataAccess.class)) {

            propsStatic.when(PropertiesDataAccess::getInstance).thenReturn(mockProps);
            langStatic.when(LanguageDataAccess::getInstance).thenReturn(mockLang);

            LanguageModel model = LanguageModel.getInstance();

            assertEquals("en", model.getCurrentLanguage());
            assertEquals(Locale.forLanguageTag("en"), model.getCurrentLocale());

            verify(mockLang, times(1)).init(Locale.forLanguageTag("en"));

            assertEquals("Hello", model.translate("greeting"));
            assertEquals("Bye", model.translate("bye"));

            assertSame(fakeBundle, model.getResourceBundle());
            assertEquals(supported, model.getSupportedLanguages());
        }
    }
}

package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.LanguageModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageControllerTest {

    @BeforeEach
    void setUp() throws Exception {
        try {
            Field ctrl = LanguageController.class.getDeclaredField("myself");
            ctrl.setAccessible(true);
            ctrl.set(null, null);

            Field model = LanguageModel.class.getDeclaredField("myself");
            model.setAccessible(true);
            model.set(null, null);
        } catch (NoSuchFieldException ignored) {}
    }

    @AfterEach
    void tearDown() throws Exception {
        try {
            Field ctrl = LanguageController.class.getDeclaredField("myself");
            ctrl.setAccessible(true);
            ctrl.set(null, null);

            Field model = LanguageModel.class.getDeclaredField("myself");
            model.setAccessible(true);
            model.set(null, null);
        } catch (NoSuchFieldException ignored) {}
    }

    @Test
    void testTranslateDelegatesToLanguageModel() {
        LanguageModel mockModel = mock(LanguageModel.class);
        when(mockModel.translate("greet")).thenReturn("Hello");

        try (MockedStatic<LanguageModel> mockedStatic = mockStatic(LanguageModel.class)) {
            mockedStatic.when(LanguageModel::getInstance).thenReturn(mockModel);

            LanguageController controller = LanguageController.getInstance();

            String res = controller.translate("greet");
            assertEquals("Hello", res);

            verify(mockModel, times(1)).translate("greet");
        }
    }

    @Test
    void testGetCurrentLanguageAndSupportedLanguages() {
        LanguageModel mockModel = mock(LanguageModel.class);
        when(mockModel.getCurrentLanguage()).thenReturn("it");
        when(mockModel.getSupportedLanguages()).thenReturn(List.of("Italian", "English"));

        try (MockedStatic<LanguageModel> mockedStatic = mockStatic(LanguageModel.class)) {
            mockedStatic.when(LanguageModel::getInstance).thenReturn(mockModel);

            LanguageController controller = LanguageController.getInstance();

            assertEquals("it", controller.getCurrentLanguage());
            assertEquals(List.of("Italian", "English"), controller.getSupportedLanguages());

            verify(mockModel, times(1)).getCurrentLanguage();
            verify(mockModel, times(1)).getSupportedLanguages();
        }
    }
}

package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.PreferencesBusinessInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesControllerTest {

    private PreferencesBusinessInterface mockPreferences;
    private PreferencesController controller;

    @BeforeEach
    void setUp() {
        mockPreferences = mock(PreferencesBusinessInterface.class);
        controller = new PreferencesController(mockPreferences);
    }

    @Test
    void testGettersDelegateToPreferences() {
        when(mockPreferences.getCurrentLanguage()).thenReturn("it");
        when(mockPreferences.getLogAreaFont()).thenReturn("Monaco-12");
        when(mockPreferences.getOutputAreaFont()).thenReturn("Consolas-11");
        when(mockPreferences.getCmdLineFont()).thenReturn("Courier-10");
        when(mockPreferences.getLogAreaLines()).thenReturn(50);
        when(mockPreferences.getOutputAreaLines()).thenReturn(30);
        when(mockPreferences.getCmdLineColumns()).thenReturn(120);

        assertEquals("it", controller.getCurrentLanguage());
        assertEquals("Monaco-12", controller.getLogAreaFont());
        assertEquals("Consolas-11", controller.getOutputAreaFont());
        assertEquals("Courier-10", controller.getCmdLineFont());
        assertEquals(50, controller.getLogAreaLines());
        assertEquals(30, controller.getOutputAreaLines());
        assertEquals(120, controller.getCmdLineColumns());

        verify(mockPreferences, times(1)).getCurrentLanguage();
        verify(mockPreferences, times(1)).getLogAreaFont();
        verify(mockPreferences, times(1)).getOutputAreaFont();
        verify(mockPreferences, times(1)).getCmdLineFont();
        verify(mockPreferences, times(1)).getLogAreaLines();
        verify(mockPreferences, times(1)).getOutputAreaLines();
        verify(mockPreferences, times(1)).getCmdLineColumns();
    }

    @Test
    void testSavePreferencesDelegatesCorrectly() {
        controller.savePreferences("en", "A", "B", "C", 10, 20, 40);

        ArgumentCaptor<String> langCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> logFontCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> outFontCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cmdFontCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> logLinesCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> outLinesCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> cmdColsCap = ArgumentCaptor.forClass(Integer.class);

        verify(mockPreferences, times(1)).savePreferences(
                langCap.capture(),
                logFontCap.capture(),
                outFontCap.capture(),
                cmdFontCap.capture(),
                logLinesCap.capture(),
                outLinesCap.capture(),
                cmdColsCap.capture()
        );

        assertEquals("en", langCap.getValue());
        assertEquals("A", logFontCap.getValue());
        assertEquals("B", outFontCap.getValue());
        assertEquals("C", cmdFontCap.getValue());
        assertEquals(10, logLinesCap.getValue());
        assertEquals(20, outLinesCap.getValue());
        assertEquals(40, cmdColsCap.getValue());
    }
}

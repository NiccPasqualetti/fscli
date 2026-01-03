package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.SerialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SerialControllerTest {

    private SerialController serialController;
    private SerialService serialServiceMock;

    @BeforeEach
    void setUp() {
        serialServiceMock = mock(SerialService.class);
        serialController = new SerialController(serialServiceMock);
    }

    @Test
    void testSaveCallsServiceSave() throws IOException {
        serialController.save();

        verify(serialServiceMock, times(1)).save();
        verifyNoMoreInteractions(serialServiceMock);
    }

    @Test
    void testSaveAsCallsServiceSaveAs() throws IOException {
        File mockFile = mock(File.class);

        serialController.saveAs(mockFile);

        verify(serialServiceMock, times(1)).saveAs(mockFile);
        verifyNoMoreInteractions(serialServiceMock);
    }

    @Test
    void testOpenCallsServiceOpen() throws IOException {
        File mockFile = mock(File.class);

        serialController.open(mockFile);

        verify(serialServiceMock, times(1)).open(mockFile);
        verifyNoMoreInteractions(serialServiceMock);
    }

    @Test
    void testIsAlreadySavedReturnsServiceResult() {
        when(serialServiceMock.isAlreadySaved()).thenReturn(true);

        assertTrue(serialController.isAlreadySaved(), "Dovrebbe restituire TRUE se il servizio restituisce TRUE");

        when(serialServiceMock.isAlreadySaved()).thenReturn(false);

        assertFalse(serialController.isAlreadySaved(), "Dovrebbe restituire FALSE se il servizio restituisce FALSE");

        verify(serialServiceMock, times(2)).isAlreadySaved();
        verifyNoMoreInteractions(serialServiceMock);
    }
}
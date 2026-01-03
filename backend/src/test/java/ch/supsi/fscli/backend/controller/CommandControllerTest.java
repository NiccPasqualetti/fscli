package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.exceptions.CommandUnknownException;
import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.commands.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommandControllerTest {

    private CommandController commandController;
    private Command mockCommandLs;
    private Command mockCommandCd;

    @BeforeEach
    void setUp() {
        CreateFilesystemService createFilesystemServiceMock = mock(CreateFilesystemService.class);

        mockCommandLs = mock(Command.class);
        when(mockCommandLs.getName()).thenReturn("ls");

        mockCommandCd = mock(Command.class);
        when(mockCommandCd.getName()).thenReturn("cd");

        List<Command> commands = List.of(mockCommandLs, mockCommandCd);

        commandController = new CommandController(commands, createFilesystemServiceMock);
    }

    @Test
    void testParse_CommandUnknownThrowsException() throws FileSystemException {
        String command = "unknowncommand";
        String[] args = {};

        assertThrows(CommandUnknownException.class, () -> commandController.parse(command, args));

        verify(mockCommandLs, never()).execute(anyList(), anyList());
        verify(mockCommandCd, never()).execute(anyList(), anyList());
    }

    @Test
    void testParse_CorrectlySeparatesOperandsAndFlags() throws FileSystemException, CommandUnknownException {
        String command = "ls";
        String[] args = {"-a", "file1", "-l", "file2", "-i"};

        String expectedResult = "Lista generata con successo";
        when(mockCommandLs.execute(anyList(), anyList())).thenReturn(expectedResult);

        String actualResult = commandController.parse(command, args);

        assertEquals(expectedResult, actualResult);

        ArgumentCaptor<List<String>> operandsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> flagsCaptor = ArgumentCaptor.forClass(List.class);

        verify(mockCommandLs, times(1)).execute(operandsCaptor.capture(), flagsCaptor.capture());

        List<String> capturedOperands = operandsCaptor.getValue();
        List<String> capturedFlags = flagsCaptor.getValue();

        assertEquals(2, capturedOperands.size());
        assertEquals("file1", capturedOperands.get(0));
        assertEquals("file2", capturedOperands.get(1));

        assertEquals(3, capturedFlags.size());
        assertEquals("a", capturedFlags.get(0));
        assertEquals("l", capturedFlags.get(1));
        assertEquals("i", capturedFlags.get(2));
    }

    @Test
    void testParse_EmptyArgs() throws FileSystemException, CommandUnknownException {
        String command = "cd";
        String[] args = {};

        commandController.parse(command, args);

        ArgumentCaptor<List<String>> operandsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<String>> flagsCaptor = ArgumentCaptor.forClass(List.class);

        verify(mockCommandCd, times(1)).execute(operandsCaptor.capture(), flagsCaptor.capture());

        List<String> capturedOperands = operandsCaptor.getValue();
        List<String> capturedFlags = flagsCaptor.getValue();

        assertEquals(0, capturedOperands.size());
        assertEquals(0, capturedFlags.size());
    }

    @Test
    void testExpandOperand_NoWildcard_ReturnsOriginal() throws FileSystemException {
        String operand = "/home/test/file.txt";
        List<String> result = commandController.expandOperand(operand);

        assertEquals(1, result.size());
        assertEquals(operand, result.get(0));
    }
}
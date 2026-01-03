package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.TooManyArgumentsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpServiceTest {

    private HelpService helpService;
    private final String description = "Mostra l'aiuto per tutti i comandi disponibili.";
    private final List<String> descriptions = Arrays.asList(
            "ls: Elenca i contenuti della directory.",
            "cd: Cambia la directory corrente.",
            "mkdir: Crea una nuova directory."
    );

    @BeforeEach
    void setUp() {
        helpService = new HelpService(description, descriptions);
    }

    @Test
    void testExecute_NoArguments() throws Exception {
        List<String> operands = Collections.emptyList();
        List<String> flags = Collections.emptyList();

        String result = helpService.execute(operands, flags);

        String expectedOutput = "ls: Elenca i contenuti della directory.\n" +
                "cd: Cambia la directory corrente.\n" +
                "mkdir: Crea una nuova directory.\n";

        assertNotNull(result);
        assertEquals(expectedOutput, result, "L'output dovrebbe contenere tutte le descrizioni separate da newline.");
    }

    @Test
    void testExecute_WithOperandsThrowsException() {
        List<String> operands = Arrays.asList("comando", "altro");
        List<String> flags = Collections.emptyList();

        assertThrows(TooManyArgumentsException.class, () -> {
            helpService.execute(operands, flags);
        }, "L'esecuzione con operandi non dovrebbe essere permessa.");
    }

    @Test
    void testExecute_WithFlagsThrowsException() {
        List<String> operands = Collections.emptyList();
        List<String> flags = List.of("-v", "--verbose");

        assertThrows(TooManyArgumentsException.class, () -> {
            helpService.execute(operands, flags);
        }, "L'esecuzione con flag non dovrebbe essere permessa.");
    }

    @Test
    void testExecute_WithBothThrowsException() {
        List<String> operands = List.of("comando");
        List<String> flags = List.of("-v");

        assertThrows(TooManyArgumentsException.class, () -> {
            helpService.execute(operands, flags);
        }, "L'esecuzione con operandi e flag non dovrebbe essere permessa.");
    }

    @Test
    void testHelpService_Initialization() {
        assertEquals("help", helpService.getName());
        assertEquals(description, helpService.getDescription());
    }
}
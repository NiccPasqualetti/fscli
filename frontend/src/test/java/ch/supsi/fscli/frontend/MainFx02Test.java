package ch.supsi.fscli.frontend;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainFx02Test extends AbstractMainGUITest {

    @Test
    void commandExecutionShowsOutputAndLog() {
        createFilesystem();
        runCommand("pwd");

        step("output shows current directory", () -> {
            TextArea outputView = lookup("#outputView").queryAs(TextArea.class);
            assertTrue(outputView.getText().contains("/"), "Output should contain a path");
        });

        step("log shows executed command", () -> {
            TextArea logView = lookup("#logView").queryAs(TextArea.class);
            String logText = logView.getText();
            assertTrue(
                    logText.contains("Executed command: pwd") || logText.contains("Comando eseguito: pwd"),
                    "Log should contain executed command message"
            );
        });
    }

    private void createFilesystem() {
        step("create filesystem", () -> {
            clickOn("#fileMenu");
            clickOn("#newMenuItem");
        });
    }

    private void runCommand(String command) {
        step("run command: " + command, () -> {
            TextField commandLine = lookup("#commandLine").queryAs(TextField.class);
            interact(commandLine::clear);
            clickOn("#commandLine");
            write(command);
            clickOn("#enter");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }
}

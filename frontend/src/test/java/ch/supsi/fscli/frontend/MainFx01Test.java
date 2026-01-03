package ch.supsi.fscli.frontend;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class MainFx01Test extends AbstractMainGUITest {

    @Test
    void commandLineRequiresFilesystem() {
        step("command line disabled by default", () -> {
            verifyThat("#commandLine", isVisible());
            TextField commandLine = lookup("#commandLine").queryAs(TextField.class);
            assertFalse(commandLine.isEditable());
            verifyThat("#enter", isDisabled());
        });

        step("create filesystem enables input", this::createFilesystem);

        step("command line ready after filesystem creation", () -> {
            TextField commandLine = lookup("#commandLine").queryAs(TextField.class);
            assertTrue(commandLine.isEditable());
            Button enterButton = lookup("#enter").queryAs(Button.class);
            assertFalse(enterButton.isDisabled());
        });
    }

    @Test
    void clearAndHelpCommandsUpdateUi() {
        createFilesystem();

        runCommand("pwd");
        step("output shows current directory", () ->
                verifyThat("#outputView", hasText(containsString("/")))
        );

        runCommand("clear");
        step("clear command wipes output area", () ->
                verifyThat("#outputView", hasText(""))
        );

        runCommand("help");
        step("help lists available commands", () -> {
            TextArea outputView = lookup("#outputView").queryAs(TextArea.class);
            String outputText = outputView.getText();
            assertTrue(outputText.contains("pwd -> to print the current working directory"));
            assertTrue(outputText.contains("clear -> to clear the output area"));
        });

        step("log shows executed help command", () -> {
            TextArea logView = lookup("#logView").queryAs(TextArea.class);
            assertTrue(logView.getText().contains("Executed command: help"));
        });
    }

    private void createFilesystem() {
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
    }

    private void runCommand(String command) {
        step("execute command: " + command, () -> {
            TextField commandLine = lookup("#commandLine").queryAs(TextField.class);
            interact(commandLine::clear);
            clickOn("#commandLine");
            write(command);
            clickOn("#enter");
        });
    }
}

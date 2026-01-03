package ch.supsi.fscli.frontend;

import javafx.scene.control.MenuBar;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class MainFx00Test extends AbstractMainGUITest {

    @Test
    public void walkThrough() {
        testMainScene();
        testCommand();
    }

    private void testMainScene() {
        step("main scene... ", ()->{
            MenuBar menuBar = lookup(".menu-bar").query();
            verifyThat(menuBar, isVisible());
            verifyThat("File", NodeMatchers.isVisible());
            verifyThat("Edit", NodeMatchers.isVisible());
            verifyThat("Help", NodeMatchers.isVisible());
        });
        step("main scene...", () -> {
            verifyThat("#fileMenu", isVisible());
            verifyThat("#editMenu", isVisible());
            verifyThat("#helpMenu", isVisible());
        });
    }

    private void testCommand() {
        step("command...", () -> {
            clickOn("#fileMenu");
            clickOn("#newMenuItem");
            clickOn("#commandLine");
            write("pwd");
            clickOn("#enter");
            verifyThat("#outputView",TextInputControlMatchers.hasText("/\n"));
        });
    }
}

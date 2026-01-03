package ch.supsi.fscli.frontend;

import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MainFx03Test extends AbstractMainGUITest {

    @Test
    void saveItemsEnabledOnlyAfterFilesystemCreation() {
        step("save items disabled at startup", () -> {
            MenuItem save = findMenuItemById("saveMenuItem");
            MenuItem saveAs = findMenuItemById("saveAsMenuItem");
            assertTrue(save.isDisable(), "Save should be disabled before a filesystem exists");
            assertTrue(saveAs.isDisable(), "Save As should be disabled before a filesystem exists");
        });

        createFilesystem();

        step("save items enabled after new filesystem", () -> {
            MenuItem save = findMenuItemById("saveMenuItem");
            MenuItem saveAs = findMenuItemById("saveAsMenuItem");
            assertFalse(save.isDisable(), "Save should be enabled after creating filesystem");
            assertFalse(saveAs.isDisable(), "Save As should be enabled after creating filesystem");
        });
    }

    @Test
    void quitShowsConfirmationAndStayOnNo() {
        createFilesystem();

        step("trigger quit from menu", () -> {
            clickOn("#fileMenu");
            clickOn("#quitMenuItem");
        });

        step("dismiss quit confirmation", () -> {
            WaitForAsyncUtils.waitForFxEvents();
            clickOn("No");
        });

        step("app remains open after cancelling quit", () -> {
            verifyThat("#fileMenu", isVisible());
        });
    }

    private void createFilesystem() {
        step("create filesystem", () -> {
            clickOn("#fileMenu");
            clickOn("#newMenuItem");
        });
    }

    private MenuItem findMenuItemById(String id) {
        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        return menuBar.getMenus().stream()
                .flatMap(menu -> menu.getItems().stream())
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElseThrow();
    }
}

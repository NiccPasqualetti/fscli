package ch.supsi.fscli.frontend;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.logging.Logger;

abstract public class AbstractMainGUITest extends ApplicationTest {

    protected static final int SLEEP_INTERVAL = 0;

    protected static final Logger LOGGER = Logger.getAnonymousLogger();

    protected int stepNo;

    protected Stage primaryStage;

    @BeforeAll
    public static void setupSpec() {
        boolean headless = Boolean.getBoolean("headless");
        if (headless) {
            System.out.println("headless mode..." + headless);
            System.setProperty("java.awt.headless", "true");
            System.setProperty("javafx.animation.framerate", "10");
            System.setProperty("testfx.headless", "true");
            System.setProperty("testfx.robot", "glass");
        }

        boolean swRenderer = Boolean.getBoolean("sw-renderer");
        if (swRenderer) {
            System.out.println("sw rendering mode..." + swRenderer);
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.forceSW", "true");
            System.setProperty("prism.use.egl", "false");
            System.setProperty("prism.disableEGL", "true");
            System.setProperty("prism.forceGPU", "false");
        }
    }

    protected void step(final String step, final Runnable runnable) {
        ++stepNo;
        LOGGER.info("STEP" + stepNo + ":" + step);
        runnable.run();
        LOGGER.info("STEP" + stepNo + ":" + "end");
    }

    public void start(final Stage stage) throws Exception {
        this.primaryStage = stage;

        final MainFx main = new MainFx();
        main.init();
        main.start(stage);
        stage.toFront();
    }

}

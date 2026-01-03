package ch.supsi.fscli.frontend.di;

import ch.supsi.fscli.backend.controller.BackendContext;
import ch.supsi.fscli.backend.controller.CommandController;
import ch.supsi.fscli.backend.controller.CreateFilesystemController;
import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.controller.SerialController;
import ch.supsi.fscli.frontend.controller.CommandEventHandler;
import ch.supsi.fscli.frontend.controller.FilesystemController;
import ch.supsi.fscli.frontend.controller.MenuBarController;
import ch.supsi.fscli.frontend.controller.MenuBarEventHandler;
import ch.supsi.fscli.frontend.model.FilesystemModel;
import ch.supsi.fscli.frontend.notification.EventManager;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import ch.supsi.fscli.frontend.view.OutputView;
import ch.supsi.fscli.frontend.view.PreferencesMenuView;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Frontend Guice module that wires the UI together using only backend controllers.
 */
public class FrontendModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventManager.class).in(Singleton.class);
        bind(MenuBarEventHandler.class).to(MenuBarController.class).in(Singleton.class);
        bind(CommandEventHandler.class).to(FilesystemController.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    LanguageController provideLanguageController() {
        return LanguageController.getInstance();
    }

    @Provides
    @Singleton
    BackendContext provideBackendContext(LanguageController languageController) {
        return new BackendContext(languageController::translate);
    }

    @Provides
    @Singleton
    PreferencesController providePreferencesController(BackendContext backendContext) {
        return backendContext.getPreferencesController();
    }

    @Provides
    @Singleton
    CreateFilesystemController provideCreateFilesystemController(BackendContext backendContext) {
        return backendContext.getCreateFilesystemController();
    }

    @Provides
    @Singleton
    SerialController provideSerialController(BackendContext backendContext) {
        return backendContext.getSerialController();
    }

    @Provides
    @Singleton
    CommandController provideCommandController(BackendContext backendContext) {
        return backendContext.getCommandController();
    }

    @Provides
    @Singleton
    FilesystemModel provideFilesystemModel(SerialController serialController,
                                           CreateFilesystemController createFilesystemController,
                                           CommandController commandController,
                                           LanguageController languageController) {
        return new FilesystemModel(serialController, createFilesystemController, commandController, languageController);
    }

    @Provides
    @Singleton
    MenuBarController provideMenuBarController(FilesystemModel filesystemModel,
                                               EventManager eventManager,
                                               PreferencesController preferencesController,
                                               PreferencesMenuView preferencesMenuView) {
        return new MenuBarController(filesystemModel, eventManager, preferencesController, preferencesMenuView);
    }

    @Provides
    @Singleton
    FilesystemController provideFilesystemController(FilesystemModel filesystemModel, EventManager eventManager) {
        return new FilesystemController(filesystemModel, eventManager);
    }

    @Provides
    @Singleton
    MenuBarView provideMenuBarView() {
        return new MenuBarView();
    }

    @Provides
    @Singleton
    CommandLineView provideCommandLineView(PreferencesController preferencesController) {
        return new CommandLineView(preferencesController);
    }

    @Provides
    @Singleton
    OutputView provideOutputView(PreferencesController preferencesController) {
        return new OutputView(preferencesController);
    }

    @Provides
    @Singleton
    LogView provideLogView(PreferencesController preferencesController) {
        return new LogView(preferencesController);
    }

    @Provides
    @Singleton
    PreferencesMenuView providePreferencesMenuView(PreferencesController preferencesController) {
        return PreferencesMenuView.getInstance(preferencesController);
    }
}

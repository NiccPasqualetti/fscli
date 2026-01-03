package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.SerialService;
import ch.supsi.fscli.backend.service.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Wires backend services and exposes only controllers to the frontend layer.
 */
public class BackendContext {
    private final CreateFilesystemService createFilesystemService;
    private final SerialService serialService;

    private final CreateFilesystemController createFilesystemController;
    private final SerialController serialController;
    private final CommandController commandController;
    private final PreferencesController preferencesController;

    public BackendContext(Function<String, String> translator) {
        createFilesystemService = new CreateFilesystemService();
        serialService = new SerialService(createFilesystemService);
        preferencesController = new PreferencesController();

        List<String> descriptions = new ArrayList<>();
        List<Command> commands = new ArrayList<>();
        commands.add(new TouchService("description.touch", createFilesystemService));
        commands.add(new RmService("description.rm", createFilesystemService));
        commands.add(new MkdirService("description.mkdir", createFilesystemService));
        commands.add(new RmdirService("description.rmdir", createFilesystemService));
        commands.add(new CdService("description.cd", createFilesystemService));
        commands.add(new PwdService("description.pwd", createFilesystemService));
        commands.add(new LsService("description.ls", createFilesystemService));
        commands.add(new LnService("description.ln", createFilesystemService));
        commands.add(new MvService("description.mv", createFilesystemService));
        commands.add(new HelpService("description.help", descriptions));

        if (translator != null) {
            for (Command command : commands) {
                descriptions.add(translator.apply(command.getDescription()));
            }
            descriptions.add(translator.apply("description.clear"));
        }

        commandController = new CommandController(commands, createFilesystemService);
        createFilesystemController = new CreateFilesystemController(createFilesystemService);
        serialController = new SerialController(serialService);
    }

    public CreateFilesystemController getCreateFilesystemController() {
        return createFilesystemController;
    }

    public SerialController getSerialController() {
        return serialController;
    }

    public CommandController getCommandController() {
        return commandController;
    }

    public PreferencesController getPreferencesController() {
        return preferencesController;
    }
}

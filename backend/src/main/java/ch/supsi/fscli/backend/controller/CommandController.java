package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.exceptions.CommandUnknownException;
import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.NotADirectoryException;
import ch.supsi.fscli.backend.service.*;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.INode;
import ch.supsi.fscli.backend.service.commands.Command;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for parsing user input, dispatching command execution, and performing wildcard expansion.
 */
public class CommandController implements WildcardResolver {
    private final List<Command> commands;
    private final CreateFilesystemService createFilesystemService;

    public CommandController(List<Command> commands, CreateFilesystemService createFilesystemService) {
        this.commands = commands;
        this.createFilesystemService = createFilesystemService;
    }

    /**
     * Parses the user input, separates flags from operands, and executes the matching command.
     * @param command the command name entered by the user
     * @param args the raw arguments provided alongside the command
     * @return the command output, if any
     */
    public String parse(String command, String[] args) throws FileSystemException {
        List<String> flags = new ArrayList<>();
        List<String> operands = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                flags.add(arg.substring(1));
            } else {
                operands.add(arg);
            }
        }

        operands = expandOperands(operands);

        for (Command c : commands)
            if (c.getName().equals(command))
                return c.execute(operands, flags);

        throw new CommandUnknownException();
    }

    /**
     * Safe wrapper that converts backend exceptions into message keys for the frontend.
     */
    public CommandResult parseSafe(String command, String[] args) {
        try {
            return CommandResult.success(parse(command, args));
        } catch (CommandUnknownException e) {
            return CommandResult.failureWithFeedbackKey(e.getMessage());
        } catch (FileSystemException e) {
            return CommandResult.failureWithOutputKey(e.getMessage());
        } catch (Exception e) {
            return CommandResult.failureWithOutputKey("unknownError");
        }
    }

    /**
     * Expands a single operand that may contain wildcard characters.
     * @param operand the raw operand to expand
     * @return all resolved operand variants; if no wildcard is present, returns the original operand
     * @throws FileSystemException if the filesystem is not initialized or resolution fails
     */
    @Override
    public List<String> expandOperand(String operand) throws FileSystemException {
        if (!operand.contains("*")) {
            return List.of(operand);
        }
        Filesystem fs = createFilesystemService.getFilesystem();
        if (fs == null) {
            throw new FileSystemException("Filesystem not initialized");
        }
        boolean absolute = operand.startsWith(Filesystem.ROOT);
        String pathWithoutRoot = absolute ? operand.substring(1) : operand;
        String[] segments = pathWithoutRoot.isEmpty()
                ? new String[0]
                : pathWithoutRoot.split(String.valueOf(Filesystem.SEPARATOR));
        class DirState {
            final DirectoryINode dir;
            final String prefix;
            DirState(DirectoryINode dir, String prefix) {
                this.dir = dir;
                this.prefix = prefix;
            }
        }
        List<DirState> currentStates = new ArrayList<>();
        if (absolute) {
            currentStates.add(new DirState(fs.getRoot(), Filesystem.ROOT));
        } else {
            currentStates.add(new DirState(fs.getCurrentDir(), ""));
        }
        if (segments.length == 0) {
            return List.of(operand);
        }
        List<String> finalPaths = new ArrayList<>();
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            boolean last = (i == segments.length - 1);
            boolean hasWildcard = segment.contains("*");
            List<DirState> nextStates = new ArrayList<>();
            if (!hasWildcard) {
                if (segment.equals(Filesystem.CURRENT_DIR_SYMBOL)) {
                    if (last) {
                        for (DirState state : currentStates) {
                            String fullPath = joinPath(state.prefix, segment, absolute);
                            finalPaths.add(fullPath);
                        }
                        break;
                    } else {
                        nextStates.addAll(currentStates);
                    }
                } else if (segment.equals(Filesystem.PARENT_DIR_SYMBOL)) {
                    if (last) {
                        for (DirState state : currentStates) {
                            String fullPath = joinPath(state.prefix, segment, absolute);
                            finalPaths.add(fullPath);
                        }
                        break;
                    } else {
                        for (DirState state : currentStates) {
                            INode parentNode = state.dir.getParent();
                            DirectoryINode parentDir = parentNode instanceof DirectoryINode
                                    ? (DirectoryINode) parentNode
                                    : state.dir;
                            String newPrefix = joinPath(state.prefix, segment, absolute);
                            nextStates.add(new DirState(parentDir, newPrefix));
                        }
                    }
                } else {
                    if (last) {
                        for (DirState state : currentStates) {
                            String fullPath = joinPath(state.prefix, segment, absolute);
                            finalPaths.add(fullPath);
                        }
                        break;
                    } else {
                        for (DirState state : currentStates) {
                            INode child = state.dir.getChildren().get(segment);
                            if (child == null) {
                                continue;
                            }
                            if (!(child instanceof DirectoryINode)) {
                                try {
                                    throw new NotADirectoryException();
                                } catch (NotADirectoryException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            DirectoryINode childDir = (DirectoryINode) child;
                            String newPrefix = joinPath(state.prefix, segment, absolute);
                            nextStates.add(new DirState(childDir, newPrefix));
                        }
                    }
                }
            } else {
                PathMatcher matcher = FileSystems.getDefault()
                        .getPathMatcher("glob:" + segment);

                if (last) {
                    for (DirState state : currentStates) {
                        for (String childName : state.dir.getChildren().keySet()) {
                            Path childPath = java.nio.file.Paths.get(childName);
                            if (matcher.matches(childPath)) {
                                String fullPath = joinPath(state.prefix, childName, absolute);
                                finalPaths.add(fullPath);
                            }
                        }
                    }
                    break;
                } else {
                    for (DirState state : currentStates) {
                        for (var entry : state.dir.getChildren().entrySet()) {
                            String childName = entry.getKey();
                            INode childNode = entry.getValue();
                            Path childPath = java.nio.file.Paths.get(childName);
                            if (matcher.matches(childPath) && childNode instanceof DirectoryINode) {
                                DirectoryINode childDir = (DirectoryINode) childNode;
                                String newPrefix = joinPath(state.prefix, childName, absolute);
                                nextStates.add(new DirState(childDir, newPrefix));
                            }
                        }
                    }
                }
            }
            currentStates = nextStates;
            if (currentStates.isEmpty()) {
                break;
            }
        }
        if (!finalPaths.isEmpty()) {
            return finalPaths;
        }
        return List.of(operand);
    }

    /**
     * Utility to join a base path and a segment into a path string
     * that matches the way the user would type it.
     */
    private String joinPath(String base, String segment, boolean absoluteBase) {
        if (base == null || base.isEmpty()) {
            return segment;
        }
        if (base.equals(Filesystem.ROOT)) {
            return Filesystem.ROOT + segment;
        }
        return base + Filesystem.SEPARATOR + segment;
    }


}

package ch.supsi.fscli.backend.controller;

/**
 * Represents the outcome of a command execution, carrying either the output or
 * a translated message key to be shown to the user.
 */
public class CommandResult {
    private final boolean success;
    private final String output;
    private final String feedbackMessageKey;

    private CommandResult(boolean success, String output, String feedbackMessageKey) {
        this.success = success;
        this.output = output;
        this.feedbackMessageKey = feedbackMessageKey;
    }

    public static CommandResult success(String output) {
        return new CommandResult(true, output, null);
    }

    public static CommandResult failureWithOutputKey(String messageKey) {
        return new CommandResult(false, messageKey, null);
    }

    public static CommandResult failureWithFeedbackKey(String messageKey) {
        return new CommandResult(false, null, messageKey);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public String getFeedbackMessageKey() {
        return feedbackMessageKey;
    }
}

package structlab.app.command;

public record CommandResult(
    boolean success,
    String message,
    boolean exitRequested
) {
    public static CommandResult ok() {
        return new CommandResult(true, null, false);
    }

    public static CommandResult ok(String msg) {
        return new CommandResult(true, msg, false);
    }

    public static CommandResult error(String error) {
        return new CommandResult(false, error, false);
    }

    public static CommandResult exit() {
        return new CommandResult(true, null, true);
    }
}

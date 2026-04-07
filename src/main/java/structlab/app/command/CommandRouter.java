package structlab.app.command;

import java.util.HashMap;
import java.util.Map;

import structlab.app.ui.TerminalFormatter;
import structlab.app.session.ActiveStructureSession;
import structlab.app.runtime.OperationExecutionResult;

public class CommandRouter {
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public void register(String alias, CommandHandler handler) {
        handlers.put(alias.toLowerCase(), handler);
    }

    public CommandResult handle(CommandContext context, ParsedCommand parsed) {
        if (parsed.name().isEmpty()) {
            return CommandResult.ok(); // Ignore empty
        }

        CommandHandler handler = handlers.get(parsed.name());
        if (handler != null) {
            try {
                return handler.execute(context, parsed);
            } catch (Exception e) {
                TerminalFormatter.printError("Unexpected error executing " + parsed.name() + ": " + e.getMessage());
                return CommandResult.error(e.getMessage());
            }
        }

        // Implicit operation forwarder for active sessions
        if (context.sessionManager().hasActiveSession()) {
            ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
            var ops = ass.getRuntime().getAvailableOperations();
            boolean isKnownOp = ops.stream().anyMatch(o -> o.name().equalsIgnoreCase(parsed.name()));

            if (isKnownOp) {
                OperationExecutionResult result = ass.getRuntime().execute(parsed.name(), parsed.arguments());
                if (!result.success()) {
                    TerminalFormatter.printError(result.message());
                } else {
                    ass.addHistory(result);
                    TerminalFormatter.printSuccess(result.message());
                    System.out.println(TerminalFormatter.boxText("Returned", String.valueOf(result.returnedValue()), structlab.app.ui.TerminalTheme.BLUE));
                    System.out.println(TerminalFormatter.boxText("Current State", ass.getRuntime().getCurrentState(), structlab.app.ui.TerminalTheme.GREEN));
                }
                return CommandResult.ok();
            }
        }

        TerminalFormatter.printError("Unknown command/operation: '" + parsed.name() + "'");
        System.out.println("  Try typing 'help' to see valid commands.");
        return CommandResult.ok();
    }

    public Map<String, CommandHandler> getHandlers() {
        return handlers;
    }
}

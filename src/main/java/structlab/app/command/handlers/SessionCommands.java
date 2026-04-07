package structlab.app.command.handlers;

import structlab.app.command.*;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.session.ActiveStructureSession;
import structlab.app.ui.TerminalFormatter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.List;
import java.util.Optional;

public class SessionCommands {

    public static void registerAll(CommandRouter router) {

        CommandHandler openHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs() || command.arguments().size() < 2) {
                    TerminalFormatter.printError("Usage: open <structure-id> <implementation-id>");
                    return CommandResult.ok();
                }

                String sId = command.arguments().get(0);
                String iId = command.arguments().get(1);

                return openSession(context, sId, iId);
            }
            @Override
            public String getDescription() { return "Start a live interactive session (e.g., open struct-stack impl-array-stack)"; }
        };
        router.register("open", openHandler);
        router.register("use", openHandler);

        CommandHandler closeHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().hasActiveSession()) {
                    context.sessionManager().clearSession();
                    TerminalFormatter.printSuccess("Closed active session. Returning to catalog viewer.");
                } else {
                    TerminalFormatter.printInfo("No active session to close.");
                }
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Close the active interactive session"; }
        };
        router.register("close", closeHandler);
        router.register("back", closeHandler);

        CommandHandler runHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!context.sessionManager().hasActiveSession()) {
                    TerminalFormatter.printError("You must 'open' a session before running operations.");
                    return CommandResult.ok();
                }
                if (!command.hasArgs()) {
                    TerminalFormatter.printError("Usage: run <operation> [args]");
                    return CommandResult.ok();
                }

                String op = command.arguments().get(0);
                List<String> opArgs = command.arguments().subList(1, command.arguments().size());

                ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
                var result = ass.getRuntime().execute(op, opArgs);

                if (!result.success()) {
                    TerminalFormatter.printError(result.message());
                } else {
                    ass.addHistory(result);
                    TerminalFormatter.printSuccess(result.message());

                    // Phase D: UX Polish - Visual distinct state print
                    System.out.println(TerminalFormatter.boxText("Returned", String.valueOf(result.returnedValue()), structlab.app.ui.TerminalTheme.BLUE));
                    System.out.println(TerminalFormatter.boxText("Current State", structlab.render.StructureRenderer.render(ass.getRuntime().getCurrentState()), structlab.app.ui.TerminalTheme.GREEN));
                }

                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Run an operation explicitly inside an active session (e.g., run push 10)"; }
        };
        router.register("run", runHandler);
        router.register("do", runHandler);

        CommandHandler stateHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!context.sessionManager().hasActiveSession()) {
                    TerminalFormatter.printError("You must 'open' a session first.");
                    return CommandResult.ok();
                }
                ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
                System.out.println(TerminalFormatter.boxText("Current State", structlab.render.StructureRenderer.render(ass.getRuntime().getCurrentState()), structlab.app.ui.TerminalTheme.GREEN));
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Print the current physical state of the live data structure"; }
        };
        router.register("state", stateHandler);
        router.register("snapshot", stateHandler);

        CommandHandler historyHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!context.sessionManager().hasActiveSession()) {
                    TerminalFormatter.printError("You must 'open' a session first.");
                    return CommandResult.ok();
                }
                ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
                var hist = ass.getHistory();
                if (hist.isEmpty()) {
                    TerminalFormatter.printInfo("No operations executed yet.");
                    return CommandResult.ok();
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hist.size(); i++) {
                    var r = hist.get(i);
                    sb.append(String.format("[%d] %s", i + 1, r.operationName()));
                    if (r.returnedValue() != null && !r.returnedValue().equals("null")) {
                        sb.append(" -> ").append(r.returnedValue());
                    }
                    sb.append("\n");
                }
                System.out.println(TerminalFormatter.boxText("Execution Timeline", sb.toString().trim(), structlab.app.ui.TerminalTheme.CYAN));
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "View the timeline of all successfully executed operations"; }
        };
        router.register("history", historyHandler);
        router.register("log", historyHandler);

        CommandHandler traceHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!context.sessionManager().hasActiveSession()) {
                    TerminalFormatter.printError("You must 'open' a session first.");
                    return CommandResult.ok();
                }
                ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
                var hist = ass.getHistory();
                if (hist.isEmpty()) {
                    TerminalFormatter.printInfo("No traces available.");
                    return CommandResult.ok();
                }
                // Print the last execution's trace steps
                var lastResult = hist.get(hist.size() - 1);
                if (lastResult.traceSteps() == null || lastResult.traceSteps().isEmpty()) {
                    TerminalFormatter.printInfo("No rigorous trace steps captured for last operation.");
                    return CommandResult.ok();
                }

                for (var step : lastResult.traceSteps()) {
                    System.out.println(structlab.render.ConsoleTraceRenderer.render(step));
                }
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Print the full internal trace of the last executed operation"; }
        };
        router.register("trace", traceHandler);

        CommandHandler resetHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!context.sessionManager().hasActiveSession()) {
                    TerminalFormatter.printError("You must 'open' a session first.");
                    return CommandResult.ok();
                }
                ActiveStructureSession ass = (ActiveStructureSession) context.sessionManager().getActiveSession().get();
                ass.getRuntime().reset();
                ass.getHistory().clear();
                TerminalFormatter.printSuccess("Live structure has been reset to an empty state.");
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Reset the live structure to its initial empty state and clear history"; }
        };
        router.register("reset", resetHandler);
        router.register("clear", resetHandler);

        // Fallback or explicit operation pass-through:
        // Actually, the AppShell might handle direct operations. For now we use `run push 10`.
    }

    private static CommandResult openSession(CommandContext context, String sId, String iId) {
        Optional<StructureMetadata> smOpt = context.registry().getStructureById(sId);
        if (smOpt.isEmpty()) {
            return CommandResult.error("Structure '" + sId + "' not found in registry.");
        }

        List<ImplementationMetadata> impls = context.registry().getImplementationsFor(sId);
        Optional<ImplementationMetadata> imOpt = impls.stream().filter(im -> im.id().equals(iId)).findFirst();

        if (imOpt.isEmpty()) {
            return CommandResult.error("Implementation '" + iId + "' not found under structure '" + sId + "'.");
        }

        try {
            StructureRuntime runtime = RuntimeFactory.createRuntime(smOpt.get(), imOpt.get());
            ActiveStructureSession session = new ActiveStructureSession(sId, iId, runtime);
            context.sessionManager().startSession(session);

            TerminalFormatter.printSuccess("Started interactive session for " + imOpt.get().name() + ".");
            System.out.println("  Try running a command from the available operations list!");
            for (var op : runtime.getAvailableOperations()) {
                System.out.println("    " + op.usage());
            }

        } catch (Exception e) {
            return CommandResult.error("Failed to initialize physical runtime adapter. " + e.getMessage());
        }

        return CommandResult.ok();
    }
}

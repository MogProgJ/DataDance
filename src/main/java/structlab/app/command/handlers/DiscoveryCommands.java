package structlab.app.command.handlers;

import java.util.List;
import java.util.Optional;

import structlab.app.command.*;
import structlab.app.ui.TerminalFormatter;
import structlab.app.ui.TerminalTheme;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

public class DiscoveryCommands {

    public static void registerAll(CommandRouter router) {

        // --- QUIT ---
        CommandHandler quitHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return CommandResult.exit();
            }
            @Override
            public String getDescription() { return "Exit the StructLab simulator"; }
        };
        router.register("quit", quitHandler);
        router.register("exit", quitHandler);
        router.register("q", quitHandler);

        // --- CLEAR ---
        CommandHandler clearHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Clear the console screen"; }
        };
        router.register("clear", clearHandler);
        router.register("cls", clearHandler);

        // --- LIST ---
        CommandHandler listHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                printAllStructures(context);
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "List all abstract data structures in the registry"; }
        };
        router.register("list", listHandler);
        router.register("ls", listHandler);
        router.register("catalog", listHandler);

        // --- SEARCH ---
        CommandHandler searchHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs()) {
                    TerminalFormatter.printError("Missing argument. Usage: search <keyword>");
                    return CommandResult.ok();
                }
                searchStructures(context, command.arguments().get(0));
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Search for structures by keyword (e.g., LIFO, tree)"; }
        };
        router.register("search", searchHandler);
        router.register("s", searchHandler);

        // --- INFO ---
        CommandHandler infoHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs()) {
                    TerminalFormatter.printError("Missing argument. Usage: info <registry-id>");
                    return CommandResult.ok();
                }
                printStructureInfo(context, command.arguments().get(0));
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Get detailed info and complexities for a structure ID"; }
        };
        router.register("info", infoHandler);
        router.register("i", infoHandler);

        // --- STATS ---
        CommandHandler statsHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                printStats(context);
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "View total metadata registry counts"; }
        };
        router.register("stats", statsHandler);

        // --- HELP ---
        CommandHandler helpHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                printHelp(router);
                return CommandResult.ok();
            }
            @Override
            public String getDescription() { return "Print this help manual"; }
        };
        router.register("help", helpHandler);
        router.register("?", helpHandler);
    }

    // Reuse the formatting logic from the old StructLabApp core (migrated inside the handlers to keep layers clean)
    private static void printAllStructures(CommandContext ctx) {
        System.out.println("\n\u001B[36m=== Available Core Data Structures ===\u001B[0m\n");
        List<StructureMetadata> allStructures = ctx.registry().getAllStructures();

        System.out.printf("%-20s %-30s %-20s %n", "\u001B[1mCATEGORY\u001B[0m", "\u001B[1mNAME\u001B[0m", "\u001B[1mREGISTRY ID\u001B[0m");
        System.out.println("-----------------------------------------------------------------------");

        allStructures.stream().sorted((a, b) -> a.category().compareTo(b.category())).forEach(structure -> {
            System.out.printf("\u001B[33m%-20s\u001B[0m %-30s \u001B[90m%-20s\u001B[0m%n",
                "[" + structure.category() + "]", structure.name(), structure.id());
        });
        System.out.println();
    }

    private static void searchStructures(CommandContext ctx, String keyword) {
        List<StructureMetadata> results = ctx.registry().search(keyword);
        if (results.isEmpty()) {
            System.out.println("\u001B[31mNo matching structures found for: \u001B[0m" + keyword);
        } else {
            System.out.println("\n\u001B[32m=== Search Results for '" + keyword + "' ===\u001B[0m\n");
            for (StructureMetadata structure : results) {
                System.out.println(" \u001B[36m\u25b6\u001B[0m " + structure.name() + " \u001B[90m(" + structure.id() + ")\u001B[0m");
            }
            System.out.println();
        }
    }

    private static void printStructureInfo(CommandContext ctx, String id) {
        Optional<StructureMetadata> optMeta = ctx.registry().getStructureById(id);
        if (optMeta.isEmpty()) {
            TerminalFormatter.printError("Structure not found with ID: " + id);
            return;
        }

        StructureMetadata meta = optMeta.get();
        System.out.println("\n\u001B[1;34m=== " + meta.name().toUpperCase() + " ===\u001B[0m");
        System.out.println("\u001B[1mCategory:\u001B[0m    \u001B[33m" + meta.category() + "\u001B[0m");
        System.out.println("\u001B[1mDescription:\u001B[0m " + meta.description());
        System.out.println("\u001B[1mBehavior:\u001B[0m    " + meta.behavior());
        System.out.println("\u001B[1mKeywords:\u001B[0m    \u001B[36m" + String.join(", ", meta.keywords()) + "\u001B[0m");

        System.out.println("\n\u001B[1m\u25BC Available Implementations:\u001B[0m");

        List<ImplementationMetadata> impls = ctx.registry().getImplementationsFor(id);
        for (ImplementationMetadata impl : impls) {
            System.out.println("\n  \u001B[32m\u25A0 " + impl.name() + "\u001B[0m \u001B[90m[" + impl.id() + "]\u001B[0m");
            System.out.println("    " + impl.description());

            System.out.println("    \u001B[35m[ Complexity ]\u001B[0m");
            System.out.println("      Space: " + impl.spaceComplexity());
            System.out.print("      Time:  ");

            StringBuilder timeRules = new StringBuilder();
            impl.timeComplexity().forEach((op, cost) -> timeRules.append(String.format("%s = %s, ", op, cost)));
            if (timeRules.length() > 0) timeRules.setLength(timeRules.length() - 2);
            System.out.println(timeRules.toString());
        }
        System.out.println();
    }

    private static void printStats(CommandContext ctx) {
        List<StructureMetadata> all = ctx.registry().getAllStructures();
        int totalImpls = 0;
        for (StructureMetadata meta : all) {
            totalImpls += ctx.registry().getImplementationsFor(meta.id()).size();
        }
        System.out.println("\n\u001B[36m[Registry Statistics]\u001B[0m");
        System.out.println("  Abstract Structures Registered: " + all.size());
        System.out.println("  Concrete Implementations:       " + totalImpls);
        System.out.println();
    }

    private static void printHelp(CommandRouter router) {
        System.out.println("\n\u001B[1mAvailable Commands:\u001B[0m\n");
        // Print unique handlers
        router.getHandlers().entrySet().stream()
                .filter(e -> !isAlias(e.getKey())) // Custom alias filter logic
                .forEach(e -> {
            System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", e.getKey(), e.getValue().getDescription());
        });
        System.out.println("\nAliases: 'q'=quit, 'ls'=list, 's'=search, 'i'=info, '?'=help");
        System.out.println();
    }

    private static boolean isAlias(String key) {
        return key.equals("q") || key.equals("ls") || key.equals("catalog")
            || key.equals("s") || key.equals("i") || key.equals("?") || key.equals("cls") || key.equals("exit");
    }
}

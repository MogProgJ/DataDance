package structlab.app;

import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;
import structlab.registry.ImplementationMetadata;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main entry point for the StructLab Interactive Simulator (Phase 5 prep).
 */
public class StructLabApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("  Welcome to StructLab Data Simulator");
        System.out.println("=========================================\n");

        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);

        System.out.println("Type 'help' to see available commands or 'quit' to exit.\n");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("structlab> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "list":
                    printAllStructures(registry);
                    break;
                case "search":
                    if (argument.isEmpty()) {
                        System.out.println("Usage: search <keyword>");
                    } else {
                        searchStructures(registry, argument);
                    }
                    break;
                case "info":
                    if (argument.isEmpty()) {
                        System.out.println("Usage: info <structure-id>");
                    } else {
                        printStructureInfo(registry, argument);
                    }
                    break;
                case "stats":
                    printStats(registry);
                    break;
                case "clear":
                case "cls":
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    break;
                case "help":
                    printHelp();
                    break;
                case "quit":
                case "exit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }

        scanner.close();
    }

    private static void printAllStructures(InMemoryStructureRegistry registry) {
        System.out.println("\n\u001B[36m=== Available Core Data Structures ===\u001B[0m\n");
        List<StructureMetadata> allStructures = registry.getAllStructures();

        System.out.printf("%-20s %-30s %-20s %n", "\u001B[1mCATEGORY\u001B[0m", "\u001B[1mNAME\u001B[0m", "\u001B[1mREGISTRY ID\u001B[0m");
        System.out.println("-----------------------------------------------------------------------");

        // Group and sort roughly by category
        allStructures.stream()
                .sorted((a, b) -> a.category().compareTo(b.category()))
                .forEach(structure -> {
            System.out.printf("\u001B[33m%-20s\u001B[0m %-30s \u001B[90m%-20s\u001B[0m%n",
                "[" + structure.category() + "]",
                structure.name(),
                structure.id());
        });
        System.out.println();
    }

    private static void searchStructures(InMemoryStructureRegistry registry, String keyword) {
        List<StructureMetadata> results = registry.search(keyword);
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

    private static void printStructureInfo(InMemoryStructureRegistry registry, String id) {
        Optional<StructureMetadata> optMeta = registry.getStructureById(id);
        if (optMeta.isEmpty()) {
            System.out.println("\u001B[31mStructure not found with ID: \u001B[0m" + id);
            return;
        }

        StructureMetadata meta = optMeta.get();
        System.out.println("\n\u001B[1;34m=== " + meta.name().toUpperCase() + " ===\u001B[0m");
        System.out.println("\u001B[1mCategory:\u001B[0m    \u001B[33m" + meta.category() + "\u001B[0m");
        System.out.println("\u001B[1mDescription:\u001B[0m " + meta.description());
        System.out.println("\u001B[1mBehavior:\u001B[0m    " + meta.behavior());
        System.out.println("\u001B[1mKeywords:\u001B[0m    \u001B[36m" + String.join(", ", meta.keywords()) + "\u001B[0m");

        System.out.println("\n\u001B[1m\u25BC Available Implementations:\u001B[0m");

        List<ImplementationMetadata> impls = registry.getImplementationsFor(id);
        for (ImplementationMetadata impl : impls) {
            System.out.println("\n  \u001B[32m\u25A0 " + impl.name() + "\u001B[0m \u001B[90m[" + impl.id() + "]\u001B[0m");
            System.out.println("    " + impl.description());

            System.out.println("    \u001B[35m[ Complexity ]\u001B[0m");
            System.out.println("      Space: " + impl.spaceComplexity());
            System.out.print("      Time:  ");

            // Format Map properly nicely instead of default toString
            StringBuilder timeRules = new StringBuilder();
            impl.timeComplexity().forEach((op, cost) -> {
                timeRules.append(String.format("%s = %s, ", op, cost));
            });
            // remove trailing comma space
            if (timeRules.length() > 0) timeRules.setLength(timeRules.length() - 2);
            System.out.println(timeRules.toString());
        }
        System.out.println();
    }

    private static void printHelp() {
        System.out.println("\n\u001B[1mAvailable Commands:\u001B[0m");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "list", "Show a table of all available abstract data structures");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "search <term>", "Search for a structure by keyword (e.g. LIFO, FIFO, tree)");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "info <id>", "Get detailed info & Big-O complexities for a structure");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "clear / cls", "Clear the console screen");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "help", "Print this help message");
        System.out.printf("  \u001B[32m%-15s\u001B[0m - %s%n", "quit / exit", "Exit the application\n");
    }

    private static void printStats(InMemoryStructureRegistry registry) {
        List<StructureMetadata> all = registry.getAllStructures();
        int totalImpls = 0;
        for (StructureMetadata meta : all) {
            totalImpls += registry.getImplementationsFor(meta.id()).size();
        }
        System.out.println("\n\u001B[36m[Registry Statistics]\u001B[0m");
        System.out.println("  Abstract Structures Registered: " + all.size());
        System.out.println("  Concrete Implementations:       " + totalImpls);
        System.out.println();
    }
}

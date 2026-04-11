package structlab.core.graph;

import java.util.*;

/**
 * Provides preset graph examples for the algorithm lab.
 * Each preset is designed to demonstrate specific BFS/DFS behaviors.
 */
public final class GraphPresets {

    private GraphPresets() {}

    /** A named graph preset with description. */
    public record Preset(String name, String description, Graph graph, String suggestedSource) {}

    /** Returns all available presets. */
    public static List<Preset> all() {
        return List.of(
                simpleConnected(),
                binaryTree(),
                graphWithCycles(),
                sparseGraph(),
                directedAcyclic(),
                disconnected(),
                diamond(),
                grid2x3()
        );
    }

    /** Simple connected undirected graph — good baseline demo. */
    public static Preset simpleConnected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        g.addEdge("D", "E");
        g.addEdge("E", "F");
        return new Preset("Simple Connected",
                "6 nodes, 7 edges — undirected, good for basic BFS/DFS comparison",
                g, "A");
    }

    /** Binary tree structure — shows BFS layer-by-layer vs DFS depth-first behavior. */
    public static Preset binaryTree() {
        Graph g = new Graph(false);
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "4");
        g.addEdge("2", "5");
        g.addEdge("3", "6");
        g.addEdge("3", "7");
        return new Preset("Binary Tree",
                "7 nodes, tree structure — highlights BFS breadth vs DFS depth",
                g, "1");
    }

    /** Graph with cycles — shows visited-node skipping. */
    public static Preset graphWithCycles() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("C", "E");
        g.addEdge("D", "E");
        g.addEdge("D", "F");
        g.addEdge("E", "F");
        return new Preset("Graph with Cycles",
                "6 nodes, 9 edges — dense with multiple cycles",
                g, "A");
    }

    /** Sparse graph — long paths, few branches. */
    public static Preset sparseGraph() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        g.addEdge("A", "F");
        g.addEdge("F", "G");
        return new Preset("Sparse / Linear",
                "7 nodes, 6 edges — two long branches from A",
                g, "A");
    }

    /** Directed acyclic graph — topological structure. */
    public static Preset directedAcyclic() {
        Graph g = new Graph(true);
        g.addEdge("S", "A");
        g.addEdge("S", "B");
        g.addEdge("A", "C");
        g.addEdge("A", "D");
        g.addEdge("B", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        g.addEdge("D", "F");
        g.addEdge("E", "F");
        return new Preset("Directed Acyclic (DAG)",
                "7 nodes, 9 directed edges — shows directed traversal behavior",
                g, "S");
    }

    /** Disconnected graph — some nodes unreachable. */
    public static Preset disconnected() {
        Graph g = new Graph(false);
        // Component 1
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("A", "C");
        // Component 2 — isolated
        g.addEdge("X", "Y");
        g.addEdge("Y", "Z");
        return new Preset("Disconnected",
                "6 nodes, 2 components — shows partial reachability",
                g, "A");
    }

    /** Diamond/converging graph — multiple paths to same node. */
    public static Preset diamond() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        return new Preset("Diamond",
                "5 nodes — converging paths from A to D to E",
                g, "A");
    }

    /** 2×3 grid graph — regular structure showing systematic traversal. */
    public static Preset grid2x3() {
        Graph g = new Graph(false);
        // Row 1: A-B-C, Row 2: D-E-F
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("D", "E");
        g.addEdge("E", "F");
        // Vertical connections
        g.addEdge("A", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        return new Preset("2×3 Grid",
                "6 nodes, grid structure — shows systematic layer expansion",
                g, "A");
    }
}

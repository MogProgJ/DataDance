package structlab.core.graph;

import java.util.*;

/**
 * A single frame/snapshot of a graph algorithm's execution state.
 * Immutable — algorithms produce a list of these frames for playback.
 */
public record AlgorithmFrame(
        /** Which algorithm produced this frame. */
        AlgorithmType algorithm,
        /** The current step index (0-based). */
        int stepIndex,
        /** Node currently being processed, or null if not applicable. */
        String currentNode,
        /** Set of fully visited/processed nodes. */
        Set<String> visited,
        /** Frontier: queue (BFS) or stack (DFS) contents in order. */
        List<String> frontier,
        /** Discovery order so far (nodes in the order they were first visited). */
        List<String> discoveryOrder,
        /** Parent map: child → parent for the traversal tree. */
        Map<String, String> parentMap,
        /** Edges in the traversal tree (from, to). */
        Set<TraversalEdge> treeEdges,
        /** Short human-readable status message. */
        String statusMessage,
        /** Current depth/layer for BFS, or recursion depth for DFS. */
        int depth) {

    public enum AlgorithmType {
        BFS, DFS
    }

    /** Directed edge in the traversal tree. */
    public record TraversalEdge(String from, String to) {}

    /** True if this is the final frame (traversal complete). */
    public boolean isComplete() {
        return currentNode == null && !frontier.isEmpty() == false
                && statusMessage != null && statusMessage.contains("complete");
    }
}

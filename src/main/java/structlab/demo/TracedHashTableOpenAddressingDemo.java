package structlab.demo;

import structlab.core.hash.HashTableOpenAddressing;
import structlab.core.hash.HashTableOpenAddressing.OpenAddressingType;
import structlab.render.StructureRenderer;
import structlab.trace.TracedHashTableOpenAddressing;
import structlab.trace.TraceLog;
import structlab.trace.TraceStep;

/**
 * Demonstrates HashTableOpenAddressing through all three probing strategies:
 * LINEAR, QUADRATIC, and DOUBLE_HASHING.
 */
public class TracedHashTableOpenAddressingDemo {

    public static void main(String[] args) {
        System.out.println("=== Hash Table Open Addressing Demo ===\n");

        demoProbing(OpenAddressingType.LINEAR);
        demoProbing(OpenAddressingType.QUADRATIC);
        demoProbing(OpenAddressingType.DOUBLE_HASHING);
    }

    private static void demoProbing(OpenAddressingType oaType) {
        System.out.println("--- Probing Strategy: " + oaType + " ---\n");

        HashTableOpenAddressing<Integer, Integer> table = new HashTableOpenAddressing<>(oaType);
        TraceLog log = new TraceLog();
        TracedHashTableOpenAddressing<Integer, Integer> traced = new TracedHashTableOpenAddressing<>(table, log);

        // Insert some key-value pairs
        traced.put(1, 100);
        traced.put(2, 200);
        traced.put(9, 900);  // May collide with 1 depending on hash
        traced.put(17, 1700); // May collide with 1, 9

        // Lookup
        traced.get(1);
        traced.get(99);  // not found

        // ContainsKey
        traced.containsKey(9);

        // Remove and re-insert to see DELETED markers
        traced.remove(9);
        traced.put(17, 1717); // update existing

        // Print all trace steps
        for (TraceStep step : log.steps()) {
            System.out.println("[" + step.operationName() + " " + step.input() + "]");
            System.out.println("  " + step.explanation());
            System.out.println(StructureRenderer.render(step.afterState()));
        }

        System.out.println();
    }
}

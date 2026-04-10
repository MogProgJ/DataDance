package structlab.demo;

import structlab.core.hash.HashTableChaining;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedHashTableChaining;

public class TracedHashTableChainingDemo {
  public static void main(String[] args) {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    System.out.println("=== Traced HashTableChaining Demo ===\n");

    // Insert into empty buckets
    traced.put(1, 100);
    traced.put(2, 200);

    // Collision: keys that hash to the same bucket (with capacity 8)
    traced.put(9, 900);   // 9 % 8 == 1, collides with key 1

    // Update existing key
    traced.put(1, 150);

    // Lookup
    traced.get(2);
    traced.get(99);       // not found

    // Contains check
    traced.containsKey(9);

    // Remove from chain
    traced.remove(9);
    traced.remove(42);    // not found

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}

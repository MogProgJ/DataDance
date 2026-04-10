package structlab.demo;

import structlab.core.hash.HashSetCustom;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedHashSetCustom;

public class TracedHashSetCustomDemo {
  public static void main(String[] args) {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    System.out.println("=== Traced HashSetCustom Demo ===\n");

    // Add values
    traced.add(10);
    traced.add(20);
    traced.add(30);

    // Duplicate add
    traced.add(10);

    // Contains checks
    traced.contains(20);
    traced.contains(99);

    // Remove
    traced.remove(20);
    traced.remove(42);   // not found

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}

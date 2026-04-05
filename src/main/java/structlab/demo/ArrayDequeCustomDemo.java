package structlab.demo;

import structlab.core.deque.ArrayDequeCustom;

public class ArrayDequeCustomDemo {
  public static void main(String[] args) {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>(4);

    System.out.println("Initial:");
    System.out.println(deque.snapshot());
    System.out.println();

    deque.addFirst(20);
    deque.addFirst(10);
    deque.addLast(30);
    deque.addLast(40);

    System.out.println("After addFirst/addLast operations:");
    System.out.println(deque.snapshot());
    System.out.println();

    System.out.println("removeFirst(): " + deque.removeFirst());
    System.out.println(deque.snapshot());
    System.out.println();

    System.out.println("removeLast(): " + deque.removeLast());
    System.out.println(deque.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + deque.checkInvariant());
  }
}

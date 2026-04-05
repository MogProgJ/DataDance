package structlab.demo;

import structlab.core.heap.HeapPriorityQueue;

public class HeapPriorityQueueDemo {
  public static void main(String[] args) {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();

    System.out.println("Initial:");
    System.out.println(queue.snapshot());
    System.out.println();

    queue.enqueue(50);
    queue.enqueue(10);
    queue.enqueue(30);
    queue.enqueue(20);

    System.out.println("After enqueue operations:");
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("peek(): " + queue.peek());
    System.out.println("dequeue(): " + queue.dequeue());
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + queue.checkInvariant());
  }
}

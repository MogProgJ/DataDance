package structlab.demo;

import structlab.core.queue.CircularArrayQueue;

public class CircularArrayQueueDemo {
  public static void main(String[] args) {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);

    System.out.println("Initial:");
    System.out.println(queue.snapshot());
    System.out.println();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);
    System.out.println("After enqueue(10), enqueue(20), enqueue(30):");
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("dequeue(): " + queue.dequeue());
    System.out.println(queue.snapshot());
    System.out.println();

    queue.enqueue(40);
    queue.enqueue(50);
    System.out.println("After enqueue(40), enqueue(50):");
    System.out.println(queue.snapshot());
    System.out.println();

    queue.dequeue();
    System.out.println("After dequeue(40)");
    System.out.println(queue.snapshot());

    System.out.println("peek(): " + queue.peek());
    System.out.println("Invariant check: " + queue.checkInvariant());
  }
}

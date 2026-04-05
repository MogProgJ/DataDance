package structlab.demo;

import structlab.core.heap.BinaryHeap;

public class BinaryHeapDemo {
  public static void main(String[] args) {
    BinaryHeap<Integer> heap = new BinaryHeap<>();

    System.out.println("Initial:");
    System.out.println(heap.snapshot());
    System.out.println();

    heap.insert(40);
    heap.insert(10);
    heap.insert(30);
    heap.insert(20);

    System.out.println("After insert(40), insert(10), insert(30), insert(20):");
    System.out.println(heap.snapshot());
    System.out.println();

    System.out.println("peek(): " + heap.peek());
    System.out.println("extractMin(): " + heap.extractMin());
    System.out.println(heap.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + heap.checkInvariant());
  }
}

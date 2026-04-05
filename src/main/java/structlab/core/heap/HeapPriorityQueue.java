package structlab.core.heap;

public class HeapPriorityQueue<T extends Comparable<T>> {
  private final BinaryHeap<T> heap;

  public HeapPriorityQueue() {
    this.heap = new BinaryHeap<>();
  }

  public int size() {
    return heap.size();
  }

  public boolean isEmpty() {
    return heap.isEmpty();
  }

  public void enqueue(T value) {
    heap.insert(value);
  }

  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty priority queue.");
    }

    return heap.extractMin();
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty priority queue.");
    }

    return heap.peek();
  }

  public boolean checkInvariant() {
    return heap != null && heap.checkInvariant();
  }

  public String snapshot() {
    return "HeapPriorityQueue{" +
      "size=" + size() +
      ", front=" + (isEmpty() ? "null" : peek()) +
      ", heap=" + heap.snapshot() +
      '}';
  }
}

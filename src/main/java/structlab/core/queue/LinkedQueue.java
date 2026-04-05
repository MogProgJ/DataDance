package structlab.core.queue;

public class LinkedQueue<T> {
  private Node<T> front;
  private Node<T> rear;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;

    private Node(T value) {
      this.value = value;
    }
  }

  public LinkedQueue() {
    this.front = null;
    this.rear = null;
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void enqueue(T value) {
    Node<T> newNode = new Node<>(value);

    if (isEmpty()) {
      front = newNode;
      rear = newNode;
    } else {
      rear.next = newNode;
      rear = newNode;
    }

    size++;
  }

  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty queue.");
    }

    T value = front.value;
    front = front.next;
    size--;

    if (size == 0) {
      rear = null;
    }

    return value;
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty queue.");
    }

    return front.value;
  }

  public boolean checkInvariant() {
    if (size < 0) {
      return false;
    }

    if (size == 0) {
      return front == null && rear == null;
    }

    if (front == null || rear == null) {
      return false;
    }

    int counted = 0;
    Node<T> current = front;
    Node<T> last = null;

    while (current != null) {
      counted++;
      last = current;
      current = current.next;
    }

    return counted == size && last == rear;
  }

  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedQueue{");
    sb.append("size=").append(size);
    sb.append(", front=").append(isEmpty() ? "null" : front.value);
    sb.append(", rear=").append(isEmpty() ? "null" : rear.value);
    sb.append(", chain=[");

    Node<T> current = front;
    while (current != null) {
      sb.append(current.value);
      current = current.next;
      if (current != null) {
        sb.append(" -> ");
      }
    }

    sb.append("]}");
    return sb.toString();
  }
}

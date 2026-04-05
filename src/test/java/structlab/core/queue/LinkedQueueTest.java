package structlab.core.queue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedQueueTest {

  @Test
  void newQueueStartsEmpty() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void enqueueAddsElementsInOrder() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);

    assertEquals(3, queue.size());
    assertEquals(10, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueRemovesFrontElement() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);

    int removed = queue.dequeue();

    assertEquals(10, removed);
    assertEquals(2, queue.size());
    assertEquals(20, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void queueFollowsFifoOrder() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);

    assertEquals(1, queue.dequeue());
    assertEquals(2, queue.dequeue());
    assertEquals(3, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueLastElementResetsFrontAndRear() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    queue.enqueue(42);

    assertEquals(42, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueThrowsOnEmptyQueue() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    assertThrows(IllegalStateException.class, queue::dequeue);
  }

  @Test
  void peekThrowsOnEmptyQueue() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();

    assertThrows(IllegalStateException.class, queue::peek);
  }
}

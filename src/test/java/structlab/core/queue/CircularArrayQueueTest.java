package structlab.core.queue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularArrayQueueTest {

  @Test
  void newQueueStartsEmpty() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void enqueueAddsElementsInOrder() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);

    assertEquals(3, queue.size());
    assertEquals(10, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueRemovesFrontElement() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

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
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

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
  void resizesWhenFull() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(2);

    queue.enqueue(1);
    queue.enqueue(2);
    int oldCapacity = queue.capacity();

    queue.enqueue(3);

    assertTrue(queue.capacity() > oldCapacity);
    assertEquals(3, queue.size());
    assertEquals(1, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void handlesWrapAroundCorrectly() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);

    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);

    assertEquals(1, queue.dequeue());
    assertEquals(2, queue.dequeue());

    queue.enqueue(4);
    queue.enqueue(5);

    assertEquals(3, queue.peek());
    assertEquals(3, queue.dequeue());
    assertEquals(4, queue.dequeue());
    assertEquals(5, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueThrowsOnEmptyQueue() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

    assertThrows(IllegalStateException.class, queue::dequeue);
  }

  @Test
  void peekThrowsOnEmptyQueue() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();

    assertThrows(IllegalStateException.class, queue::peek);
  }
}

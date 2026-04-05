package structlab.core.heap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeapPriorityQueueTest {

  @Test
  void newPriorityQueueStartsEmpty() {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void enqueueAddsByPriorityNotInsertionOrder() {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();

    queue.enqueue(50);
    queue.enqueue(10);
    queue.enqueue(30);

    assertEquals(10, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueRemovesSmallestElementFirst() {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();

    queue.enqueue(50);
    queue.enqueue(10);
    queue.enqueue(30);
    queue.enqueue(20);

    assertEquals(10, queue.dequeue());
    assertEquals(20, queue.dequeue());
    assertEquals(30, queue.dequeue());
    assertEquals(50, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void peekThrowsOnEmptyPriorityQueue() {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();
    assertThrows(IllegalStateException.class, queue::peek);
  }

  @Test
  void dequeueThrowsOnEmptyPriorityQueue() {
    HeapPriorityQueue<Integer> queue = new HeapPriorityQueue<>();
    assertThrows(IllegalStateException.class, queue::dequeue);
  }
}

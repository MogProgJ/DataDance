package structlab.core.deque;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedDequeTest {

  @Test
  void newDequeStartsEmpty() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    assertEquals(0, deque.size());
    assertTrue(deque.isEmpty());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void addFirstWorks() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    deque.addFirst(10);
    deque.addFirst(20);

    assertEquals(2, deque.size());
    assertEquals(20, deque.peekFirst());
    assertEquals(10, deque.peekLast());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void addLastWorks() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    deque.addLast(10);
    deque.addLast(20);

    assertEquals(2, deque.size());
    assertEquals(10, deque.peekFirst());
    assertEquals(20, deque.peekLast());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void removeFirstFollowsExpectedOrder() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    deque.addLast(10);
    deque.addLast(20);
    deque.addLast(30);

    assertEquals(10, deque.removeFirst());
    assertEquals(20, deque.removeFirst());
    assertEquals(30, deque.removeFirst());
    assertTrue(deque.isEmpty());
  }

  @Test
  void removeLastFollowsExpectedOrder() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    deque.addLast(10);
    deque.addLast(20);
    deque.addLast(30);

    assertEquals(30, deque.removeLast());
    assertEquals(20, deque.removeLast());
    assertEquals(10, deque.removeLast());
    assertTrue(deque.isEmpty());
  }

  @Test
  void mixedFrontAndRearOperationsWork() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();

    deque.addFirst(20);
    deque.addFirst(10);
    deque.addLast(30);
    deque.addLast(40);

    assertEquals(10, deque.removeFirst());
    assertEquals(40, deque.removeLast());
    assertEquals(20, deque.peekFirst());
    assertEquals(30, deque.peekLast());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void removeFirstThrowsOnEmptyDeque() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    assertThrows(IllegalStateException.class, deque::removeFirst);
  }

  @Test
  void removeLastThrowsOnEmptyDeque() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    assertThrows(IllegalStateException.class, deque::removeLast);
  }
}

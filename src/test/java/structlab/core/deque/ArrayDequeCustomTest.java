package structlab.core.deque;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayDequeCustomTest {

  @Test
  void newDequeStartsEmpty() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

    assertEquals(0, deque.size());
    assertTrue(deque.isEmpty());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void addFirstWorks() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

    deque.addFirst(10);
    deque.addFirst(20);

    assertEquals(2, deque.size());
    assertEquals(20, deque.peekFirst());
    assertEquals(10, deque.peekLast());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void addLastWorks() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

    deque.addLast(10);
    deque.addLast(20);

    assertEquals(2, deque.size());
    assertEquals(10, deque.peekFirst());
    assertEquals(20, deque.peekLast());
    assertTrue(deque.checkInvariant());
  }

  @Test
  void removeFirstFollowsExpectedOrder() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

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
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

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
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();

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
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    assertThrows(IllegalStateException.class, deque::removeFirst);
  }

  @Test
  void removeLastThrowsOnEmptyDeque() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    assertThrows(IllegalStateException.class, deque::removeLast);
  }
}

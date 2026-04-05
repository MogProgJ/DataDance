package structlab.core.heap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryHeapTest {

  @Test
  void newHeapStartsEmpty() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();

    assertEquals(0, heap.size());
    assertTrue(heap.isEmpty());
    assertTrue(heap.checkInvariant());
  }

  @Test
  void insertMaintainsMinAtRoot() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();

    heap.insert(30);
    heap.insert(10);
    heap.insert(20);

    assertEquals(3, heap.size());
    assertEquals(10, heap.peek());
    assertTrue(heap.checkInvariant());
  }

  @Test
  void extractMinRemovesSmallestElement() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();

    heap.insert(30);
    heap.insert(10);
    heap.insert(20);

    int min = heap.extractMin();

    assertEquals(10, min);
    assertEquals(2, heap.size());
    assertEquals(20, heap.peek());
    assertTrue(heap.checkInvariant());
  }

  @Test
  void extractMinFollowsSortedOrder() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();

    heap.insert(40);
    heap.insert(10);
    heap.insert(30);
    heap.insert(20);

    assertEquals(10, heap.extractMin());
    assertEquals(20, heap.extractMin());
    assertEquals(30, heap.extractMin());
    assertEquals(40, heap.extractMin());
    assertTrue(heap.isEmpty());
    assertTrue(heap.checkInvariant());
  }

  @Test
  void peekThrowsOnEmptyHeap() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    assertThrows(IllegalStateException.class, heap::peek);
  }

  @Test
  void extractMinThrowsOnEmptyHeap() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    assertThrows(IllegalStateException.class, heap::extractMin);
  }
}

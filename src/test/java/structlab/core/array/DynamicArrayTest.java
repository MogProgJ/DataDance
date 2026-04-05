package structlab.core.array;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

  @Test
  void newArrayStartsEmpty() {
    DynamicArray<Integer> array = new DynamicArray<>();

    assertEquals(0, array.size());
    assertFalse(array.size() > array.capacity());
    assertTrue(array.isEmpty());
    assertTrue(array.checkInvariant());
  }

  @Test
  void appendAddsElementsInOrder() {
    DynamicArray<Integer> array = new DynamicArray<>();

    array.append(10);
    array.append(20);
    array.append(30);

    assertEquals(3, array.size());
    assertEquals(10, array.get(0));
    assertEquals(20, array.get(1));
    assertEquals(30, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void appendResizesWhenFull() {
    DynamicArray<Integer> array = new DynamicArray<>(2);

    array.append(1);
    array.append(2);
    int oldCapacity = array.capacity();

    array.append(3);

    assertTrue(array.capacity() > oldCapacity);
    assertEquals(3, array.size());
    assertEquals(1, array.get(0));
    assertEquals(2, array.get(1));
    assertEquals(3, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void insertAtMiddleShiftsElementsRight() {
    DynamicArray<Integer> array = new DynamicArray<>();

    array.append(1);
    array.append(3);
    array.insert(1, 2);

    assertEquals(3, array.size());
    assertEquals(1, array.get(0));
    assertEquals(2, array.get(1));
    assertEquals(3, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void insertAtEndBehavesLikeAppend() {
    DynamicArray<Integer> array = new DynamicArray<>();

    array.append(1);
    array.append(2);
    array.insert(2, 3);

    assertEquals(3, array.size());
    assertEquals(3, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void setReplacesElement() {
    DynamicArray<String> array = new DynamicArray<>();

    array.append("a");
    array.set(0, "b");

    assertEquals("b", array.get(0));
    assertTrue(array.checkInvariant());
  }

  @Test
  void removeAtReturnsRemovedElementAndShiftsLeft() {
    DynamicArray<Integer> array = new DynamicArray<>();

    array.append(1);
    array.append(2);
    array.append(3);

    int removed = array.removeAt(1);

    assertEquals(2, removed);
    assertEquals(2, array.size());
    assertEquals(1, array.get(0));
    assertEquals(3, array.get(1));
    assertTrue(array.checkInvariant());
  }

  @Test
  void getThrowsOnInvalidIndex() {
    DynamicArray<Integer> array = new DynamicArray<>();

    assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
  }

  @Test
  void insertThrowsOnInvalidIndex() {
    DynamicArray<Integer> array = new DynamicArray<>();

    assertThrows(IndexOutOfBoundsException.class, () -> array.insert(1, 99));
  }

  @Test
  void removeThrowsOnInvalidIndex() {
    DynamicArray<Integer> array = new DynamicArray<>();

    assertThrows(IndexOutOfBoundsException.class, () -> array.removeAt(0));
  }
}

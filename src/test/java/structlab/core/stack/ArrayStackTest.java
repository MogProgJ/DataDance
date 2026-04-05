package structlab.core.stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayStackTest {

  @Test
  void newStackStartsEmpty() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    assertEquals(0, stack.size());
    assertTrue(stack.isEmpty());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void pushAddsElementsToTop() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    stack.push(10);
    stack.push(20);
    stack.push(30);

    assertEquals(3, stack.size());
    assertEquals(30, stack.peek());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void popRemovesLastPushedElement() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    stack.push(10);
    stack.push(20);
    stack.push(30);

    int popped = stack.pop();

    assertEquals(30, popped);
    assertEquals(2, stack.size());
    assertEquals(20, stack.peek());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void stackFollowsLifoOrder() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    stack.push(1);
    stack.push(2);
    stack.push(3);

    assertEquals(3, stack.pop());
    assertEquals(2, stack.pop());
    assertEquals(1, stack.pop());
    assertTrue(stack.isEmpty());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void peekDoesNotRemoveElement() {
    ArrayStack<String> stack = new ArrayStack<>();

    stack.push("a");
    stack.push("b");

    assertEquals("b", stack.peek());
    assertEquals(2, stack.size());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void popThrowsOnEmptyStack() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    assertThrows(IllegalStateException.class, stack::pop);
  }

  @Test
  void peekThrowsOnEmptyStack() {
    ArrayStack<Integer> stack = new ArrayStack<>();

    assertThrows(IllegalStateException.class, stack::peek);
  }
}

package structlab.core.stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedStackTest {

  @Test
  void newStackStartsEmpty() {
    LinkedStack<Integer> stack = new LinkedStack<>();

    assertEquals(0, stack.size());
    assertTrue(stack.isEmpty());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void pushAddsElementsToTop() {
    LinkedStack<Integer> stack = new LinkedStack<>();

    stack.push(10);
    stack.push(20);
    stack.push(30);

    assertEquals(3, stack.size());
    assertEquals(30, stack.peek());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void popRemovesLastPushedElement() {
    LinkedStack<Integer> stack = new LinkedStack<>();

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
    LinkedStack<Integer> stack = new LinkedStack<>();

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
    LinkedStack<String> stack = new LinkedStack<>();

    stack.push("a");
    stack.push("b");

    assertEquals("b", stack.peek());
    assertEquals(2, stack.size());
    assertTrue(stack.checkInvariant());
  }

  @Test
  void popThrowsOnEmptyStack() {
    LinkedStack<Integer> stack = new LinkedStack<>();

    assertThrows(IllegalStateException.class, stack::pop);
  }

  @Test
  void peekThrowsOnEmptyStack() {
    LinkedStack<Integer> stack = new LinkedStack<>();

    assertThrows(IllegalStateException.class, stack::peek);
  }
}

package structlab.demo;

import structlab.core.stack.LinkedStack;

public class LinkedStackDemo {
  public static void main(String[] args) {
    LinkedStack<Integer> stack = new LinkedStack<>();

    System.out.println("Initial:");
    System.out.println(stack.snapshot());
    System.out.println();

    stack.push(10);
    System.out.println("After push(10):");
    System.out.println(stack.snapshot());
    System.out.println();

    stack.push(20);
    System.out.println("After push(20):");
    System.out.println(stack.snapshot());
    System.out.println();

    stack.push(30);
    System.out.println("After push(30):");
    System.out.println(stack.snapshot());
    System.out.println();

    System.out.println("peek(): " + stack.peek());
    System.out.println("After peek() (no removal):");
    System.out.println(stack.snapshot());
    System.out.println();

    System.out.println("pop(): " + stack.pop());
    System.out.println("After pop():");
    System.out.println(stack.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + stack.checkInvariant());
  }
}

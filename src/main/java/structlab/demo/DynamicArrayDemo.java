package structlab.demo;

import structlab.core.array.DynamicArray;

public class DynamicArrayDemo {
  public static void main(String[] args) {
    DynamicArray<Integer> array = new DynamicArray<>(2);

    System.out.println("Initial:");
    System.out.println(array.snapshot());
    System.out.println();

    array.append(10);
    System.out.println("After append(10):");
    System.out.println(array.snapshot());
    System.out.println();

    array.append(20);
    System.out.println("After append(20):");
    System.out.println(array.snapshot());
    System.out.println();

    array.append(30);
    System.out.println("After append(30) — resize should have happened:");
    System.out.println(array.snapshot());
    System.out.println();

    array.insert(1, 99);
    System.out.println("After insert(1, 99):");
    System.out.println(array.snapshot());
    System.out.println();

    int removed = array.removeAt(2);
    System.out.println("After removeAt(2), removed = " + removed + ":");
    System.out.println(array.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + array.checkInvariant());
  }
}

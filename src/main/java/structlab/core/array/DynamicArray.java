package structlab.core.array;

import java.util.Arrays;

public class DynamicArray<T> {
  private static final int DEFAULT_CAPACITY = 4;

  private Object[] data;
  private int size;

  public DynamicArray() {
    this(DEFAULT_CAPACITY);
  }

  public DynamicArray(int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0.");
    }

    this.data = new Object[initialCapacity];
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public int capacity() {
    return data.length;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  @SuppressWarnings("unchecked")
  public T get(int index) {
    checkIndex(index);
    return (T) data[index];
  }

  public void set(int index, T value) {
    checkIndex(index);
    data[index] = value;
  }

  public void append(T value) {
    ensureCapacityForOneMore();
    data[size] = value;
    size++;
  }

  public void insert(int index, T value) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException(
        "Index " + index + " out of bounds for size " + size);
    }

    ensureCapacityForOneMore();

    for (int i = size; i > index; i--) {
      data[i] = data[i - 1];
    }

    data[index] = value;
    size++;
  }

  @SuppressWarnings("unchecked")
  public T removeAt(int index) {
    checkIndex(index);

    T removed = (T) data[index];

    for (int i = index; i < size - 1; i++) {
      data[i] = data[i + 1];
    }

    data[size - 1] = null;
    size--;

    return removed;
  }

  public boolean checkInvariant() {
    return data != null
      && data.length > 0
      && size >= 0
      && size <= data.length;
  }

  public String snapshot() {
    Object[] logical = new Object[size];

    for (int i = 0; i < size; i++) {
      logical[i] = data[i];
    }

    return "DynamicArray{" +
      "size=" + size +
      ", capacity=" + data.length +
      ", elements=" + Arrays.toString(logical) +
      ", raw=" + Arrays.toString(data) +
      '}';
  }

  private void ensureCapacityForOneMore() {
    if (size == data.length) {
      resize(data.length * 2);
    }
  }

  private void resize(int newCapacity) {
    Object[] newData = new Object[newCapacity];

    for (int i = 0; i < size; i++) {
      newData[i] = data[i];
    }

    data = newData;
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
        "Index " + index + " out of bounds for size " + size);
    }
  }
}

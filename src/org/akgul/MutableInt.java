package org.akgul;

public class MutableInt {
  private int counter = 1;

  public MutableInt() {
  }

  static MutableInt createMutableInt() {
    return new MutableInt();
  }

  public void increment() {
    setCounter(getCounter() + 1);
  }

  public int getCounter() {
    return this.counter;
  }

  public String toString() {
    return String.format("%d", this.getCounter());
  }

  void setCounter(final int counter) {
    this.counter = counter;
  }
}

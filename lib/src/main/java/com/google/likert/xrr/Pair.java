package com.google.likert.xrr;

/** An immutable pair of data. */
public class Pair<A, B> {
  public final A first;
  public final B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  /** Creates a new pair containing the given elements in order. */
  public static <A, B> Pair<A, B> of(A first, B second) {
    return new Pair<>(first, second);
  }

  /** Returns the first element of this pair. */
  public A getFirst() {
    return first;
  }

  /** Returns the second element of this pair. */
  public B getSecond() {
    return second;
  }
}

package com.sirekam.pattern.iterator;

public interface Iterator<T> {
    boolean hasNext();
    T next();
}
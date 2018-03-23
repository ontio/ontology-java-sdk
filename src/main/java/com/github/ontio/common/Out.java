package com.github.ontio.common;

/**
 * To simulate out keyword in c#
 */
public class Out<T> {
    private T obj;

    public T get() {
        return obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}

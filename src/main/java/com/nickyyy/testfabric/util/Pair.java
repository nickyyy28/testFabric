package com.nickyyy.testfabric.util;

public class Pair <T>{
    public T var1;
    public T var2;
    public Pair(T a, T b) {
        var1 = a;
        var2 = b;
    }

    public T getAnother(T var) {
        if (var == var1) return var2;
        if (var == var2) return var1;
        return null;
    }
}

/*
 * Developed  by Kiran Yedavalli on 8/7/18 12:27 PM
 * Last Modified 8/6/18 2:33 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.utils;

@FunctionalInterface
public interface TriFunction<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}

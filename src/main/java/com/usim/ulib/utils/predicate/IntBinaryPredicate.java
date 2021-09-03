package com.usim.ulib.utils.predicate;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface IntBinaryPredicate extends BiPredicate<Integer, Integer> {
    boolean check(int left, int right);

    @Override
    default boolean test(Integer left, Integer right) {
        return check(left, right);
    }
}

package com.usim.ulib.utils.api;

import java.util.concurrent.Semaphore;

@FunctionalInterface
public interface Event {
    Semaphore getSemaphore();

    default void cancel() {
        var semaphore = getSemaphore();
        semaphore.drainPermits();
        semaphore.release();
    }

    default void accept() {
        getSemaphore().release();
    }
}

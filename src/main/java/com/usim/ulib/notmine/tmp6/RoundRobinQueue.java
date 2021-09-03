package com.usim.ulib.notmine.tmp6;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinQueue extends ArrayList<Customer> {

    private final AtomicInteger current;
    private final Semaphore waitingLock;

    public RoundRobinQueue() {
        current = new AtomicInteger();
        waitingLock = new Semaphore(0);
    }

    @Override
    public boolean add(Customer customer) {
        waitingLock.release();
        return super.add(customer);
    }

    public void openLock() {
        waitingLock.release(Integer.MAX_VALUE - waitingLock.availablePermits());
    }

    public Customer invokeCustomer(AtomicBoolean inWorkingTime) {
        try {
            waitingLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isEmpty())
            return null;
        var customer = get(current.getAndIncrement() % size());
        while (inWorkingTime.get() && customer.isDone())
            customer = get(current.getAndIncrement() % size());
        customer.start();
        return customer;
    }
}

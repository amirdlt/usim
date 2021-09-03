package com.usim.ulib.notmine.tmp6;

import com.usim.ulib.utils.ThreadLike;

import java.util.concurrent.atomic.AtomicBoolean;

public class Employee extends ThreadLike {
    private static int index = 0;

    private final int id;
    private final Bank bank;
    private final RoundRobinQueue queue;
    private final AtomicBoolean inWorkingTime;

    public Employee(Bank bank, EmployeeTask task) {
        super("Employee=" + index++ + ":" + bank + ":" + task);
        this.bank = bank;
        id = index - 1;
        queue = new RoundRobinQueue();
        inWorkingTime = new AtomicBoolean(false);
    }

    public synchronized void addToQueue(Customer newCustomer) {
        queue.add(newCustomer);
    }

    public Bank getBank() {
        return bank;
    }

    public int getId() {
        return id;
    }

    public RoundRobinQueue getQueue() {
        return queue;
    }

    @Override
    public void start() {
        inWorkingTime.set(true);
        System.out.println(threadName + " Say Hi!");
        super.start();
    }

    @Override
    public void join() {
        inWorkingTime.set(false);
        queue.openLock();
        super.join();
        queue.forEach(ThreadLike::join);
        System.out.println(threadName + " Say Goodbye!");
    }

    @Override
    public void run() {
        while (inWorkingTime.get()) {
            var customer = queue.invokeCustomer(inWorkingTime);
            if (!inWorkingTime.get())
                return;
            if (customer.isDone())
                return;
            System.out.println(threadName + " doing task for " + customer.getThreadName());
            customer.join();
            System.out.println(threadName + " end task(part) for " + customer.getThreadName());
        }
    }
}

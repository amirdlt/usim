package com.usim.ulib.notmine.tmp6;

import com.usim.ulib.utils.ThreadLike;

import static java.lang.Math.random;

public class Customer extends ThreadLike {
    private static int index = 0;

    private final int id;
    private int neededTime;
    private boolean done;
    private final EmployeeTask job;

    public Customer() {
        super("Customer=" + index++);
        id = index - 1;
        done = false;
        neededTime = (int) (random() * 5000);
        job = EmployeeTask.values()[(int) (random() * 3)];
    }

    public boolean isDone() {
        return done;
    }

    public EmployeeTask getJob() {
        return job;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            if (neededTime <= 0)
                return;

            if (neededTime <= 3_000) {
                System.out.println(threadName + " doing " + job + " remained time: " + neededTime);
                Thread.sleep(neededTime);
                neededTime = 0;
                done = true;
                System.out.println(threadName + " job ended.");
            } else {
                System.out.println(threadName + " doing " + job + " remained time: " + neededTime);
                Thread.sleep(3_000);
                neededTime -= 3000;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return threadName + " " + isDone() + " Remaining time: " + neededTime;
    }
}

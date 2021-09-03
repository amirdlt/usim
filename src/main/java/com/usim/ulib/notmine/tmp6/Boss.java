package com.usim.ulib.notmine.tmp6;

import com.usim.ulib.utils.ThreadLike;

import java.time.Duration;

public class Boss extends ThreadLike {
    private static int index = 0;
    private static final Duration bankWorkDuration = Duration.ofSeconds(10);

    private final int id;
    private final Bank bank;

    public Boss(Bank bank) {
        super("Boss=" + index++);
        this.bank = bank;
        id = index - 1;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        System.out.println(threadName + " Say Hi! Start working...");
        try {
            bank.start();
            Thread.sleep(bankWorkDuration.toMillis());
            System.out.println(threadName + " Say Stop! Stop working...");
            bank.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

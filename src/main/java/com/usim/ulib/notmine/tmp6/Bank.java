package com.usim.ulib.notmine.tmp6;

import com.usim.ulib.utils.ThreadLike;

import java.util.Map;

public class Bank extends ThreadLike {
    private static int index = 0;

    private final int id;
    private final Map<EmployeeTask, Employee> employees;
    private final Boss boss;

    public Bank() {
        super("Bank=" + index++);

        id = index - 1;
        boss = new Boss(this);
        employees = Map.of(
                EmployeeTask.REGISTRATION, new Employee(this, EmployeeTask.REGISTRATION),
                EmployeeTask.MONEY_ADDER, new Employee(this, EmployeeTask.MONEY_ADDER),
                EmployeeTask.MONEY_GETTER, new Employee(this, EmployeeTask.MONEY_GETTER)
        );
    }

    public Boss getBoss() {
        return boss;
    }

    @Override
    public void join() {
        employees.values().forEach(ThreadLike::join);
        super.join();
        System.out.println(threadName + " closed");
    }

    public void addCustomer(Customer customer) {
        employees.get(customer.getJob()).addToQueue(customer);
    }

    public int getId() {
        return id;
    }

    public void showEmployeesQueue() {
        System.out.println(" +++++ " + threadName + " +++++");
        employees.values().forEach(e -> {
            System.out.println("***   ---> " + e.getThreadName() + " queue: ");
            int counter = 1;
            for (var c : e.getQueue())
                System.out.println(counter++ + ") " + c);
            System.out.println("*".repeat(25));
        });
    }

    @Override
    public void run() {
        System.out.println(threadName + " opened");
        employees.values().forEach(ThreadLike::start);
    }

    @Override
    public String toString() {
        return threadName;
    }
}

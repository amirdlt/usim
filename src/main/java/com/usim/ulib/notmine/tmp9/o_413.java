package com.usim.ulib.notmine.tmp9;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class o_413 extends Teacher {
    private String name;
    private int salary;
    private int hoursOfWork;

    public o_413() {
        this("", 0);
    }

    public o_413(String name, int salary) {
        this(name, salary, 0);
    }

    public o_413(String name, int salary, int hoursOfWork) {
        this.name = name;
        this.salary = salary;
        this.hoursOfWork = hoursOfWork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getHoursOfWork() {
        return hoursOfWork;
    }

    public void setHoursOfWork(int hoursOfWork) {
        this.hoursOfWork = hoursOfWork;
    }

    @Override
    public List<Teacher> readAll() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter number of Teachers: ");
        int num = Integer.parseInt(scanner.nextLine());
        List<Teacher> teachers = new ArrayList<>(num);
        while (num-- > 0) {
            System.out.println("please enter name: ");
            String name = scanner.nextLine();
            System.out.println("Please enter salary:");
            int salary = Integer.parseInt(scanner.nextLine());
            System.out.println("Please enter hours of work:");
            int w = Integer.parseInt(scanner.nextLine());
            teachers.add(new o_413(name, salary, w));
        }
        return teachers;
    }

    @Override
    public String toString() {
        return "o_413{" + "name='" + name + '\'' + ", salary=" + salary + ", hoursOfWork=" + hoursOfWork + '}';
    }
}

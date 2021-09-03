package com.usim.ulib.notmine.proj6;

public class Employee {
    private int salary;
    private final String firstName;
    private final String lastName;
    private final int id;

    public Employee(int salary, String firstName, String lastName, int id) {
        this.salary = salary;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void raiseSalary(double percent) {
        salary = (int) (salary * (1 + percent / 100));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return lastName + " " + lastName;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Employee{" + "salary=" + salary + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
                + ", id=" + id + '}';
    }
}

package com.usim.ulib.notmine.proj6;

public class EmployeeTest {
    public static void main(String[] args) {
         var employee = new Employee(100, "Karina", "Allahveran", 10);
        System.out.println(employee);
        employee.setSalary(346543);
        employee.raiseSalary(45);
        System.out.println(employee);
        System.out.println(employee.getFirstName());
        System.out.println(employee.getName());
        System.out.println(employee.getSalary());
    }
}

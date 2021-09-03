package com.usim.ulib.notmine.proj6;

public class CustomerTest {
    public static void main(String[] args) {
        var customer = new Customer(12, "Karina", 100);
        System.out.println(customer);
        System.out.println(customer.getDiscount());
        customer.setDiscount(17);
        System.out.println(customer.getDiscount());
        System.out.println(customer.getId());
    }
}

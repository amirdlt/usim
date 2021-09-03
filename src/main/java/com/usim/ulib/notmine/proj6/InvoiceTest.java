package com.usim.ulib.notmine.proj6;

public class InvoiceTest {
    public static void main(String[] args) {
        var invoice = new Invoice(100, new Customer(12, "KA", 30), 2000);
        System.out.println(invoice);
        System.out.println(invoice.getAmountAfterDiscount());
        System.out.println(invoice.getCustomerDiscount());
        System.out.println(invoice.getCustomerId());
        System.out.println(invoice.getCustomerName());
        invoice.setCustomer(new Customer(35, "KA", 1520));
        System.out.println(invoice);
    }
}

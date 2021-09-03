package com.usim.ulib.notmine.tmp6;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Math.random;

public class Main {
    public static void main(String[] args) {
        var banks = new Bank[4];
        Arrays.setAll(banks, i -> new Bank());

        var scanner = new Scanner(System.in);
        System.out.println("Please enter number of customers: ");
        var numOfCustomers = Integer.parseInt(scanner.nextLine().trim());

        for (var bank : banks)
            bank.getBoss().start();

        while (numOfCustomers-- > 0)
            banks[(int) (random() * 4)].addCustomer(new Customer());

        for (var bank : banks)
            bank.getBoss().join();

        System.out.println("\n".repeat(5));

        System.out.println("----- End -----");

        for (var bank : banks)
            bank.showEmployeesQueue();
    }
}

package com.usim.ulib.notmine.proj2;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main10(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter value of x: ");
        double x = scanner.nextDouble();
        System.out.println("Please enter value of m: ");
        double m = scanner.nextDouble();
        System.out.println("Please enter value of p: ");
        double p = scanner.nextDouble();
        System.out.println("The result y = x * 2 / (m + p) is: " + (x * 2 / (m + p)));

        System.out.println("Please enter value of x: ");
        x = scanner.nextDouble();
        System.out.println("Please enter value of m: ");
        m = scanner.nextDouble();
        System.out.println("Please enter value of k: ");
        double k = scanner.nextDouble();
        System.out.println("Please enter value of r: ");
        double r = scanner.nextDouble();
        System.out.println("The result y = x + m^2 - k / (r + 2) is: " + (x + m*m - k / (r + 2)));
        scanner.close();
    }

    public static void main0(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter radius of the circle: ");
        double radius = scanner.nextDouble();
        System.out.println("The perimeter of the circle is: " + 2 * radius * Math.PI);
        System.out.println("The area of the circle is: " + radius * radius * Math.PI);
        System.out.println("The area of the sphere is: " + 4 * radius * radius * Math.PI);
        System.out.println("The volume of the sphere is: " + 4 / 3 * radius * radius * radius * Math.PI);
        scanner.close();
    }

    public static void main1(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the minutes: ");
        int minutes = scanner.nextInt();
        System.out.println("Equivalence to: " + (minutes / 60) + ":" + (minutes % 60));
        scanner.close();
    }

    public static void main2(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter value of payment: ");
        int payment = scanner.nextInt();
        System.out.println("Please enter Duration of repayments: ");
        int durationOfRepayments = scanner.nextInt();
        System.out.println("Please enter Percent of benefit: ");
        double percentOfBenefits = scanner.nextDouble();
        System.out.println("Final total benefits of the bank: " + (int) (payment * percentOfBenefits));
        System.out.println("The monthly repayments are: " + (int) ((1 + percentOfBenefits) * payment / durationOfRepayments));
        scanner.close();
    }

    public static void main3(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter price of the book: ");
        int price = scanner.nextInt();
        System.out.println("Please enter percent of off: ");
        double offPercent = scanner.nextDouble();
        System.out.println("The final price of the book is: " + (int) (price * offPercent));
        scanner.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter date of today in yyyy/mm/dd format: ");
        int[] today = Arrays.stream(scanner.nextLine().trim().split("/")).mapToInt(Integer::parseInt).toArray();
        System.out.println("Please enter date of expire in yyyy/mm/dd format: ");
        int[] expire = Arrays.stream(scanner.nextLine().trim().split("/")).mapToInt(Integer::parseInt).toArray();
        System.out.println("Number of remaining days is: " +
                ((expire[0]-today[0])*365 + (expire[1]-today[1])*30 + expire[2]-today[2]));
    }
}

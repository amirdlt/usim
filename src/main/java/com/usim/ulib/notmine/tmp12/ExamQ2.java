package com.usim.ulib.notmine.tmp12;

import java.util.Scanner;

public class ExamQ2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter number: ");
        int n = Integer.parseInt(scanner.nextLine());
        String result = num(n / 1000) + " Thousands";
        int temp = (n / 100) % 10;
        if (temp != 0)
            result += " and " + num(temp) + " Hundreds";
        temp = (n / 10) % 10;
        if (temp != 0 && temp != 1)
            result += " and " + num(temp) + "ty";
        if (temp == 1) {
            result += " and " + num(n % 100);
        } else {
            temp = n % 10;
            if (temp != 0)
                result += " " + num(temp);
        }
        System.out.println(result);
    }

    private static String num(int n) {
        switch (n) {
            case 1:
                return "One";
            case 2:
                return "Two";
            case 3:
                return "Three";
            case 4:
                return "Four";
            case 5:
                return "Five";
            case 6:
                return "Six";
            case 7:
                return "Seven";
            case 8:
                return "Eight";
            case 9:
                return "Nine";
            case 10:
                return "Ten";
            case 11:
                return "Eleven";
            case 12:
                return "Twelve";
            case 13:
                return "Thirteen";
            case 14:
                return "Fourteen";
            case 15:
                return "Fifteen";
            case 16:
                return "Sixteen";
            case 17:
                return "Seventeen";
            case 18:
                return "Eighteen";
            case 19:
                return "Nineteen";
            default:
                return "";
        }

    }
}

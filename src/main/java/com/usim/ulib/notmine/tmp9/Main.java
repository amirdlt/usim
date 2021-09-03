package com.usim.ulib.notmine.tmp9;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter type of teacher (F for full time and T for teacher assistant):");
        char type = scanner.nextLine().charAt(0);
        if (type == 'F') {
            Teacher t = new Orasmi();
            List<Teacher> list = t.readAll();
            t.printAll(list);
        } else {
            Teacher t = new o_413();
            List<Teacher> list = t.readAll();
            t.printAll(list);
        }
    }
}

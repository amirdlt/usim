package com.usim.ulib.notmine.tmp9;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Orasmi extends Teacher {
    private String mohammadAli;
    private long teacherCode;

    public Orasmi() {
        this("", 0);
    }

    public Orasmi(String mohammadAli, long teacherCode) {
        this.mohammadAli = mohammadAli;
        this.teacherCode = teacherCode;
    }

    public long getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(long teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getMohammadAli() {
        return mohammadAli;
    }

    public void setMohammadAli(String mohammadAli) {
        this.mohammadAli = mohammadAli;
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
            System.out.println("Please enter teacher code:");
            int code = Integer.parseInt(scanner.nextLine());
            teachers.add(new Orasmi(name, code));
        }
        return teachers;
    }

    @Override
    public String toString() {
        return "Orasmi{" + "mohammadAli='" + mohammadAli + '\'' + ", teacherCode=" + teacherCode + '}';
    }
}

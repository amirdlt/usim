package com.usim.ulib.notmine.proj9;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Student[] students = Student.loadStudents("studentFile.txt");
        Teacher[] teachers = Teacher.loadTeachers("teacherFile.txt");

        System.out.println("Please enter the date for penalty calculating: ");
        String date = new Scanner(System.in).nextLine();

        Student maxStudent = students[0];
        Teacher maxTeacher = teachers[0];
        for (Student student : students) {
            if (maxStudent.penalty(date) < student.penalty(date))
                maxStudent = student;
            if (student.penalty(date) == 0)
                continue;
            System.out.println("Student id:" + student.getStudentId() + " has penalty: " + student.penalty(date));
        }

        for (Teacher teacher : teachers) {
            if (maxTeacher.penalty(date) < teacher.penalty(date))
                maxTeacher = teacher;
            if (teacher.penalty(date) == 0)
                continue;
            System.out.println("Teacher code:" + teacher.getCode() + " has penalty: " + teacher.penalty(date));
        }

        System.out.println("Student with most penalty id: " + maxStudent.getStudentId() + " with: " + maxStudent.penalty(date) + " penalty.");
        System.out.println("Teacher with most penalty code: " + maxTeacher.getCode() + " with: " + maxTeacher.penalty(date) + " penalty.");
    }
}

package com.usim.ulib.notmine.proj9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Student extends Member {
    private final int studentId;

    public Student(String name, MemberType type, int studentId) {
        super(name, type);
        this.studentId = studentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public static Student[] loadStudents(String source) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(source));
        Student[] res = new Student[Integer.parseInt(scanner.nextLine().trim())];
        int count = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("*"))
                continue;
            int id = Integer.parseInt(line.split(" ")[1].trim());
            MemberType type = scanner.nextLine().trim().split(" ")[1].startsWith("und") ?
                    MemberType.STUDENT_BACHELOR :
                    MemberType.STUDENT_SENIOR;
            int numOfItems = Integer.parseInt(scanner.nextLine().split(" ")[1].trim());
            Student student = new Student("", type, id);
            while (numOfItems-- > 0) {
                String name = scanner.nextLine().split(" ")[1];
                student.borrowItem(Items.getItemByName(name), scanner.nextLine().split(" ")[1]);
            }
            res[count++] = student;
        }
        return res;
    }
}

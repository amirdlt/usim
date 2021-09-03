package com.usim.ulib.notmine.proj9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Teacher extends Member {
    private final int code;

    public Teacher(String name, int code) {
        super(name, MemberType.TEACHER);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Teacher[] loadTeachers(String source) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(source));
        Teacher[] res = new Teacher[Integer.parseInt(scanner.nextLine().trim())];
        int count = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("*"))
                continue;
            int id = Integer.parseInt(line.split(" ")[1].trim());
            int numOfItems = Integer.parseInt(scanner.nextLine().split(" ")[1].trim());
            Teacher teacher = new Teacher("", id);
            while (numOfItems-- > 0) {
                String name = scanner.nextLine().split(" ")[1];
                teacher.borrowItem(Items.getItemByName(name), scanner.nextLine().split(" ")[1]);
            }
            res[count++] = teacher;
        }
        return res;
    }
}

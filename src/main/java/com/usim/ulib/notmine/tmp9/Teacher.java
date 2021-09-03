package com.usim.ulib.notmine.tmp9;

import java.util.List;

public abstract class Teacher {
    public abstract List<Teacher> readAll();
    public void printAll(List<Teacher> teachers) {
        for (Teacher t : teachers) {
            System.out.println(t);
        }
    }
}

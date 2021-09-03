package com.usim.ulib.notmine.proj10;

public class BachelorStudent extends Student {
    public BachelorStudent(String name, String studentId) {
        super(name, studentId);
    }

    @Override
    protected String type() {
        return BACHELOR;
    }
}

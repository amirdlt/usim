package com.usim.ulib.notmine.proj10;

public class MastersStudent extends Student {
    public MastersStudent(String name, String studentId) {
        super(name, studentId);
    }

    @Override
    protected String type() {
        return MASTERS;
    }
}

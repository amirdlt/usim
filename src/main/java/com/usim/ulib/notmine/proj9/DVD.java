package com.usim.ulib.notmine.proj9;

public class DVD extends Item {
    private final int id;

    public DVD(int id, String name) {
        super(name);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

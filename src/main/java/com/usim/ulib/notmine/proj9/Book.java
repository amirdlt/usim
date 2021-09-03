package com.usim.ulib.notmine.proj9;

public class Book extends Item {
    private final String wName;
    private final int numOfPages;

    public Book(String name, String wName, int numOfPages) {
        super(name);
        this.wName = wName;
        this.numOfPages = numOfPages;
    }

    public String getWName() {
        return wName;
    }

    public int getNumOfPages() {
        return numOfPages;
    }
}

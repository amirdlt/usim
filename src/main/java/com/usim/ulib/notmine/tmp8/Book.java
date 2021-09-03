package com.usim.ulib.notmine.tmp8;

import java.util.Date;

public class Book extends Document {
    private final String title;

    public Book(Date date, String title) {
        super(date);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Book{" + "title='" + title + '\'' + '}' + super.toString();
    }
}

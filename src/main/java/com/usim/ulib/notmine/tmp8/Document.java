package com.usim.ulib.notmine.tmp8;

import java.util.Arrays;
import java.util.Date;

public class Document {
    private final String[] authors;
    private final Date date;
    private int authorIndex;

    public Document(Date date) {
        this.date = date;
        authors = new String[100];
        authorIndex = 0;
    }

    public String[] getAuthors() {
        return Arrays.copyOfRange(authors, 0, authorIndex);
    }

    public Date getDate() {
        return date;
    }

    public void addAuthor(String name) {
        authors[authorIndex++] = name;
    }

    @Override
    public String toString() {
        return "Document{" + "authors=" + Arrays.toString(getAuthors()) + ", date=" + date + '}';
    }
}

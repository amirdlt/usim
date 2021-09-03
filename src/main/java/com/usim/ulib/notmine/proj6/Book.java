package com.usim.ulib.notmine.proj6;

public class Book {
    private final String name;
    private final int price;
    private final Author author;
    private int qty;

    public Book(String name, int price, Author author, int qty) {
        this.name = name;
        this.price = price;
        this.author = author;
        this.qty = qty;
    }

    public Book(String name, int price, Author author) {
        this(name, price, author, 0);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Author getAuthor() {
        return author;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "Book{" + "name='" + name + '\'' + ", price=" + price + ", author=" + author + ", qty=" + qty + '}';
    }
}

package com.usim.ulib.notmine.proj6;

public class BookTest {
    public static void main(String[] args) {
        var book = new Book("karinaBook", 1000, new Author("Karina", "k@gmail.com", 'M'), 10);
        System.out.println(book.getAuthor());
        System.out.println(book.getName());
        System.out.println(book);
        System.out.println(book.getQty());
        book.setQty(200);
        System.out.println(book);
    }
}

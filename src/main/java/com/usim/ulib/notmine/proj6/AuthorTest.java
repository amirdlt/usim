package com.usim.ulib.notmine.proj6;

public class AuthorTest {
    public static void main(String[] args) {
        var author = new Author("Karina", "karina@gmail.com", 'M');
        System.out.println(author.getEmail());
        System.out.println(author);
        System.out.println(author.getGender());
        System.out.println(author.getName());
        author.setEmail("karina2@gmail.com");
        System.out.println(author.getEmail());
    }
}

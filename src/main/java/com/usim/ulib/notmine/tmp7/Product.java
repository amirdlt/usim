package com.usim.ulib.notmine.tmp7;

public class Product {
    private final int id;
    private int price;
    private int numOfSells;
    private String name;

    public Product(int id, String name, int price, int numOfSells) {
        this.id = id;
        this.price = price;
        this.numOfSells = numOfSells;
        this.name = name;
    }

    public Product() {
        this((int) (Math.random() * Integer.MAX_VALUE), "unknown", 0, 0);
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumOfSells() {
        return numOfSells;
    }

    public void setNumOfSells(int numOfSells) {
        this.numOfSells = numOfSells;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void showPrice() {
        System.out.println("The price of " + name + price);
    }

    public static void main(String[] args) {
        Product product = new Product(123, "Ice Cream", 1000, 10);

        product.showPrice();

        System.out.println(product.getName());
    }
}

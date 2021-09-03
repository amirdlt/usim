package com.usim.ulib.notmine.tmp5;

public class Node {
    private final long timestamps;

    private int data;

    private Node next;

    public Node(int data) {
        this.data = data;
        timestamps = System.currentTimeMillis();
        next = null;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}

package com.usim.ulib.notmine.tmp12;

public class Node {
    private int data;
    private Node next;
    public Node(int data) { setData(data); setNext(null); }
    public void setData(int data) { this.data = data; }
    public int getData() { return data; }
    public void setNext(Node next) { this.next = next;}
    public Node getNext() { return next;}
    public String toString() { return String.format("[%d]", data); }
}

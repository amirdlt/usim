package com.usim.ulib.notmine.tmp5;

public class LinkedList {
    private Node head;

    private int size;

    public LinkedList(int[] arr) {
        this();
        for (int i : arr)
            add(i);
    }

    public LinkedList() {
        size = 0;
        head = null;
    }

    public void add(int newData) {
        if (head == null) {
            head = new Node(newData);
            size++;
            return;
        }

        Node head = this.head;
        while (head.getNext() != null)
            head = head.getNext();
        head.setNext(new Node(newData));
        size++;
    }

    public int get(int i) {
        Node head = this.head;
        while (i-- > 0)
            head = head.getNext();
        return head.getData();
    }

    public void remove(int i) {
        if (i == 0) {
            head = head.getNext();
            size--;
            return;
        }
        Node head = this.head;
        while (--i > 0)
            head = head.getNext();
        head.setNext(head.getNext().getNext());
        size--;
    }

    public int[] toArray() {
        int[] res = new int[size];
        Node head = this.head;
        int counter = 0;
        while (head != null) {
            res[counter++] = head.getData();
            head = head.getNext();
        }
        return res;
    }

    private Node getNode(int i) {
        Node head = this.head;
        while (i-- > 0)
            head = head.getNext();
        return head;
    }

    public void removeOlds() {
        for (int i = 0; i < size; i++)
            if (System.currentTimeMillis() - getNode(i).getTimestamps() > 60_000)
                remove(i);
    }

    public void perform(Operation operation) {
        Node head = this.head;
        while (head != null) {
            operation.perform(head);
            head = head.getNext();
        }
    }
}

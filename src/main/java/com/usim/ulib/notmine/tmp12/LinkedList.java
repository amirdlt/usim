package com.usim.ulib.notmine.tmp12;

public class LinkedList {
    private int count;

    private Node firstNode;

    public LinkedList() {
        count = 0;
        firstNode = null;
    }
    public int length() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void removeDuplicates() {
        var first = firstNode;
        while (first != null) {
            var value = first.getData();
            var next = first.getNext();
            while (next != null) {
                if (next.getData() == value)
                    removeNode(next);
                next = next.getNext();
            }
            first = first.getNext();
        }
    }

    private void removeNode(Node node) {
        if (firstNode == node) {
            firstNode = node.getNext();
            count--;
            return;
        }
        var pre = firstNode;
        var first = firstNode.getNext();
        while (first != node) {
            pre = first;
            first = first.getNext();
        }
        pre.setNext(node.getNext());
        count--;
    }

    public void divide() {
        var head = firstNode;
        while (head != null) {
            var data = head.getData() / 2;
            head.setData(data);
            var next = new Node(data);
            next.setNext(head.getNext());
            head.setNext(next);
            head = next.getNext();
        }
    }
}

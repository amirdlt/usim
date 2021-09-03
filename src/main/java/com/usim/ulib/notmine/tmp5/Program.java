package com.usim.ulib.notmine.tmp5;

public class Program {
    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 10; i++)
            list.add((int) (Integer.MAX_VALUE * Math.random()));
        list.perform(new PrintOperation());
    }
}

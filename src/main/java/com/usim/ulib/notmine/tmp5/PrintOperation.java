package com.usim.ulib.notmine.tmp5;

public class PrintOperation implements Operation {

    @Override
    public void perform(Node node) {
        System.out.println("Timestamps: " + node.getTimestamps() + " Data: " + node.getData());
    }
}
